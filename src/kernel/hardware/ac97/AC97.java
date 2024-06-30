package kernel.hardware.ac97;

import kernel.Memory;
import kernel.hardware.PCI;
import kernel.io.console.Console;

public class AC97 {
  public final static int SAMPLE_RATE = 48000;

  // milliseconds for one buffer descriptor
  private final static int MS_PER_BUFFER_DESC = 30;

  // the lower the faster we can react to events
  public final static int SAMPLES_PER_BUFFER_DESC = SAMPLE_RATE / 1000 * MS_PER_BUFFER_DESC;

  private final static int BUFFER_DESC_COUNT = 32;

  public final static int PCM_SCRATCH_SIZE = SAMPLES_PER_BUFFER_DESC * BUFFER_DESC_COUNT;

  private static int bus, device, function;
  private static boolean foundPciFunction = false;
  private static long[] bufferDescriptorListMemory;

  public static short[] pcmScratch;

  public static int sampleIndex;

  private static int writeIndex = 0;
  
  /*
   * Native Audio Mixer Base and Native Audio Bus Master Base
   */
  private static int namBase, nabmBase;

  public static boolean setup() {
    PCI.resetIterator();

    while (PCI.next()) {
      if (PCI.getBaseClassCode() == PCI.BaseClassCode.MultimediaDevice
          && PCI.getSubClassCode() == 0x1) {
        bus = PCI.bus;
        device = PCI.device;
        function = PCI.function;
        foundPciFunction = true;
        break;
      }
    }

    if (!foundPciFunction) {
      return false; // could not find AC97
    }

    // AC97 is still set in PCI
    PCI.enableBusMasterForCurrentDevice();

    int headerType = PCI.getHeaderType();
    if (headerType != 0x0) return false;

    // we need BAR 0 and BAR 1 which are both IO BARs
    namBase = PCI.readBar(0);
    nabmBase = PCI.readBar(1);

    Console.print("\nNAM Base: ");
    Console.printHex(namBase);
    Console.print("\nNABM Base: ");
    Console.printHex(nabmBase);

    powerCard(); // TODO: also set other flags?

    resetRegisters();

    setPcmOutputVolumeToFull();
    setMasterOutputVolumeToFull();

    int capabilities = readCapabilities();
    Console.print("\nCapabilities: ");
    Console.printHex(capabilities);
    if ((capabilities & 0x10) != 0)
      Console.print("\nsupports headphones");
    
    // Pulse-code modulation (PCM)
    // use sample rate 48000
    // Load sound data in memory
    // 48000 samples per second

    pcmScratch = new short[PCM_SCRATCH_SIZE];

    // fillSamplesWithSquare(pcmScratch, SAMPLES_PER_BUFFER_DESC * BUFFER_DESC_COUNT);

    // has to be 4 byte aligned, but this is the case
    // with our memory management
    bufferDescriptorListMemory = new long[32];

    int bufferDescriptorListAddress = MAGIC.addr(bufferDescriptorListMemory[0]);
    BufferDescriptorList bufferDescriptorList = (BufferDescriptorList)MAGIC.cast2Struct(bufferDescriptorListAddress);
    //bufferDescriptorList.descriptors[0].soundPtr = MAGIC.addr(samples[0]);
    //bufferDescriptorList.descriptors[0].sampleCount = (short)0xFFFE; // max
    //bufferDescriptorList.descriptors[0].flags = (short)(1 << 14); // last entry of buffer

    for (int i = 0; i < 32; i++) {
      bufferDescriptorList.descriptors[i].soundPtr = MAGIC.addr(pcmScratch[i * SAMPLES_PER_BUFFER_DESC]);
      bufferDescriptorList.descriptors[i].sampleCount = (short)SAMPLES_PER_BUFFER_DESC;
      bufferDescriptorList.descriptors[i].flags = 0;
    }

    // reset stream
    MAGIC.wIOs8(
      nabmBase + NabmRegisters.NABM_REGISTER_PCM_OUT + NabmRegisterBox.TRANSFER_CONTROL,
      (byte)0x2);
    
    while (true) {
      int val = MAGIC.rIOs8(nabmBase + NabmRegisters.NABM_REGISTER_PCM_OUT + NabmRegisterBox.TRANSFER_CONTROL);
      if ((val & 0x2) != 0x2) break;
    }

    MAGIC.wIOs8(
      nabmBase + NabmRegisters.NABM_REGISTER_PCM_OUT + NabmRegisterBox.TRANSFER_CONTROL,
      (byte)0x0);

    MAGIC.wIOs32(
      nabmBase + NabmRegisters.NABM_REGISTER_PCM_OUT + NabmRegisterBox.BUFFER_DESCRIPTOR_LIST_ADDRESS,
      bufferDescriptorListAddress);

    MAGIC.wIOs8(
      nabmBase + NabmRegisters.NABM_REGISTER_PCM_OUT + NabmRegisterBox.LAST_ENTRY_INDEX,
      (byte)31);

    MAGIC.wIOs8(
      nabmBase + NabmRegisters.NABM_REGISTER_PCM_OUT + NabmRegisterBox.TRANSFER_CONTROL,
      (byte)0x1);
    
    Console.println();

    return true;
  }

  @SJC.Inline
  public static void writeSample(short sample) {
    pcmScratch[writeIndex] = sample;
    writeIndex++;
    writeIndex %= PCM_SCRATCH_SIZE;
  }

  public static void run() {
      byte index = MAGIC.rIOs8(
        nabmBase + NabmRegisters.NABM_REGISTER_PCM_OUT + NabmRegisterBox.CURRENTLY_PROCESSED_ENTRY_INDEX
      );

      // -1 -> 31
      byte lastIndex = (byte)((index - 1) & 0x1F);

      MAGIC.wIOs8(
        nabmBase + NabmRegisters.NABM_REGISTER_PCM_OUT + NabmRegisterBox.LAST_ENTRY_INDEX,
        lastIndex
      );

      short sampleCount = MAGIC.rIOs16(
        nabmBase + NabmRegisters.NABM_REGISTER_PCM_OUT + NabmRegisterBox.CURRENT_ENTRY_TRANSFERRED_SAMPLE_COUNT
      );

      AC97.sampleIndex = index * SAMPLES_PER_BUFFER_DESC + sampleCount;
  }

  public static void setWriteIndex(int index) {
    if (index < 0 || index >= PCM_SCRATCH_SIZE) index = 0;

    AC97.writeIndex = index;
  }

  private static void powerCard() {
    MAGIC.wIOs32(nabmBase + NabmRegisters.GLOBAL_CONTROL_REGISTER, 0x2);
  }

  private static void resetRegisters() {
    // write any value to reset registers
    MAGIC.wIOs32(namBase + NamRegisters.RESET_REGISTERS, 0xFF);
  }

  private static int readCapabilities() {
    return MAGIC.rIOs32(namBase + NamRegisters.CAPABILITIES);
  }

  private static void setPcmOutputVolume(short volume) {
    MAGIC.wIOs16(namBase + NamRegisters.SET_PCM_OUTPUT_VOLUME, volume);
  }

  private static void setPcmOutputVolumeToFull() {
    setPcmOutputVolume((short)0);
  }

  private static void setMasterOutputVolume(short volume) {
    MAGIC.wIOs16(namBase + NamRegisters.SET_MASTER_OUTPUT_VOLUME, volume);
  }

  private static void setMasterOutputVolumeToFull() {
    setMasterOutputVolume((short)0);
  }
}