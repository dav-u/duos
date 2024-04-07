package kernel.interrupt;

import rte.*;

public class Interrupts {
  private final static int MASTER = 0x20, SLAVE = 0xA0;

  /// End of Interrupt
  private final static byte EOI = 0x20;

  /// Interrupts are disables per default.
  /// Use setInterruptFlag to enable.
  private static boolean interruptsEnabled = false;

  /// Reference to the interrupt table memory so it does not get garbage collected.
  private static long[] interruptTableMemory;

  public static InterruptDescriptorTable interruptDescriptorTable;

  public static void createInterruptTable() {
    // One long is 8 bytes, like one entry.
    // Max of 256 entries possible.
    // We also already set everything to zero, so every interrupt has P = 0 (is disabled)
    interruptTableMemory = new long[256];

    int tableStartAddress = MAGIC.addr(interruptTableMemory[0]);

    interruptDescriptorTable = (InterruptDescriptorTable)MAGIC.cast2Struct(tableStartAddress);

    fillInterruptTable();
    loadIdt(tableStartAddress, 50 * 8); // 255 * 8
  }

  public static void acknowledgePicMasterInterrupt() {
    MAGIC.wIOs8(MASTER, EOI);
  }

  public static void acknowledgePicSlaveInterrupt() {
    MAGIC.wIOs8(MASTER, EOI);
    MAGIC.wIOs8(SLAVE, EOI);
  }

  /// Enables interrupts
  public static void setInterruptFlag() {
    interruptsEnabled = true;
    MAGIC.inline(0xFB); // STI
  }

  /// Disables interrupts
  public static void clearInterruptFlag() {
    MAGIC.inline(0xFA); // CLI
    interruptsEnabled = false;
  }

  /// Returns whether interrupts are currently enabled
  public static boolean areInterruptsEnabled() {
    return interruptsEnabled;
  }

  /// Load Interrupt-Descriptor-Table
  public static void loadIdt(int baseAddress, int tableLimit) {
    long tmp=(((long)baseAddress)<<16)|(long)tableLimit;
    MAGIC.inline(0x0F, 0x01, 0x5D); MAGIC.inlineOffset(1, tmp); // lidt [ebp-0x08/tmp]
  }

  /// Initializes the PIC (Programmable Interrupt Controller).
  /// The PIC controls interrupts from external hardware (like the keyboard)
  public static void initPic() {
    programmChip(MASTER, 0x20, 0x04); //init offset and slave config of master
    programmChip(SLAVE, 0x28, 0x02); //init offset and slave config of slave
  }

  private static void programmChip(int port, int offset, int icw3) {
    MAGIC.wIOs8(port++, (byte)0x11); // ICW1
    MAGIC.wIOs8(port, (byte)offset); // ICW2
    MAGIC.wIOs8(port, (byte)icw3); // ICW3
    MAGIC.wIOs8(port, (byte)0x01); // ICW4
  }

  public static void setInterruptHandler(int interrupt, SClassDesc handlersClassDescriptor, int methodOffset) {
    interruptDescriptorTable.entries[interrupt].segmentSelector = (short)(1 << 3); // CS segment from SJC;

    int code = MAGIC.rMem32(MAGIC.cast2Ref(handlersClassDescriptor) + methodOffset);
    code += MAGIC.getCodeOff();

    interruptDescriptorTable.entries[interrupt].offsetLow = (short)(code & 0xFFFF);
    interruptDescriptorTable.entries[interrupt].offsetHigh = (short)(code >>> 16);

    // meta data is set as follows:
    // 0b1 00  0  1110  000 00000 -> 1000 1110 0000 0000 -> 0x8E00
    //   P DPL 0  gate  000  res  (gate is 32 bit interrupt gate)
    interruptDescriptorTable.entries[interrupt].metaData = (short)0x8E00;
  }

  private static void fillInterruptTable() {
    SClassDesc handlersClassDesc = MAGIC.clssDesc("Handlers");
    setInterruptHandler(0x00, handlersClassDesc, MAGIC.mthdOff("Handlers", "DivideError"));
    setInterruptHandler(0x01, handlersClassDesc, MAGIC.mthdOff("Handlers", "DebugException"));
    setInterruptHandler(0x02, handlersClassDesc, MAGIC.mthdOff("Handlers", "NonMaskableInterrupt"));
    setInterruptHandler(0x03, handlersClassDesc, MAGIC.mthdOff("Handlers", "Breakpoint"));
    setInterruptHandler(0x04, handlersClassDesc, MAGIC.mthdOff("Handlers", "Into"));
    setInterruptHandler(0x05, handlersClassDesc, MAGIC.mthdOff("Handlers", "IndexOutOfRange"));
    setInterruptHandler(0x06, handlersClassDesc, MAGIC.mthdOff("Handlers", "InvalidOpcode"));
    setInterruptHandler(0x08, handlersClassDesc, MAGIC.mthdOff("Handlers", "DoubleFault"));
    setInterruptHandler(0x0D, handlersClassDesc, MAGIC.mthdOff("Handlers", "GeneralProtectionError"));
    setInterruptHandler(0x0E, handlersClassDesc, MAGIC.mthdOff("Handlers", "PageFault"));
    setInterruptHandler(0x20, handlersClassDesc, MAGIC.mthdOff("Handlers", "Timer"));
    setInterruptHandler(0x21, handlersClassDesc, MAGIC.mthdOff("Handlers", "Keyboard"));
  }
}