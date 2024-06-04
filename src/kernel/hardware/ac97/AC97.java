package kernel.hardware.ac97;

import kernel.Memory;
import kernel.hardware.PCI;
import kernel.io.console.Console;

public class AC97 {
  private final static int SAMPLE_RATE = 48000;
  private static int bus, device, function;
  private static boolean foundPciFunction = false;
  private static long[] bufferDescriptorListMemory;
  
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

    // Console.print("\nBus: ");
    // Console.printHex(bus);
    // Console.print("\nDevice: ");
    // Console.printHex(device);
    // Console.print("\nFunction: ");
    // Console.printHex(function);

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

    // int globalControl = MAGIC.rIOs32(nabmBase + NabmRegisters.GLOBAL_CONTROL_REGISTER);

    // Console.print("\nGlobal control register: ");
    // Console.printHex(globalControl);

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

    // 1 second of samples
    short[] samples = new short[SAMPLE_RATE];
    fillSamplesWithSquare(samples);

    // has to be 4 byte aligned, but this is the case
    // with our memory management
    bufferDescriptorListMemory = new long[32];

    int bufferDescriptorListAddress = MAGIC.addr(bufferDescriptorListMemory[0]);
    BufferDescriptorList bufferDescriptorList = (BufferDescriptorList)MAGIC.cast2Struct(bufferDescriptorListAddress);
    //bufferDescriptorList.descriptors[0].soundPtr = MAGIC.addr(samples[0]);
    //bufferDescriptorList.descriptors[0].sampleCount = (short)0xFFFE; // max
    //bufferDescriptorList.descriptors[0].flags = (short)(1 << 14); // last entry of buffer

    for (int i = 0; i < 32; i++) {
      bufferDescriptorList.descriptors[i].soundPtr = MAGIC.addr(samples[0]);
      bufferDescriptorList.descriptors[i].sampleCount = (short)SAMPLE_RATE;
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

    MAGIC.wIOs32(
      nabmBase + NabmRegisters.NABM_REGISTER_PCM_OUT + NabmRegisterBox.ENTRY_COUNT,
      32);

    MAGIC.wIOs8(
      nabmBase + NabmRegisters.NABM_REGISTER_PCM_OUT + NabmRegisterBox.TRANSFER_CONTROL,
      (byte)0x1);
    
    Console.println();

    return true;
  }

  private static void fillSamplesWithSquare(short[] samples) {
    int freq = 880; //Hz
    int samplesPerWave = SAMPLE_RATE / freq;
    boolean highFrequency = false;

    for (int i = 0; i < SAMPLE_RATE; i++) {
      if (i % samplesPerWave == 0) highFrequency = !highFrequency;

      samples[i] = highFrequency ? (short)0xF000 : (short)0;
    }
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