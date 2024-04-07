package kernel.interrupt;

public class InterruptDescriptor extends STRUCT {
  /// Low part of the address of the interrupt service routine
  @SJC(offset=0)
  public short offsetLow;

  /// Code segment from the GDT
  @SJC(offset=2)
  public short segmentSelector;

  /// Lower byte is reserved -> set to zero.
  /// Next four bit are the gate type (highest of the four bit specifies gate size):
  ///   - 0b0101 or 0x5: Task Gate, note that in this case, the Offset value is unused and should be set to zero.
  ///   - 0b0110 or 0x6: 16-bit Interrupt Gate
  ///   - 0b0111 or 0x7: 16-bit Trap Gate
  ///   - 0b1110 or 0xE: 32-bit Interrupt Gate
  ///   - 0b1111 or 0xF: 32-bit Trap Gate 
  /// Next bit has to be zero.
  /// Next two bits are DPL (CPU Privilege Level) and are ignored by hardware
  /// interrupts (only relevant for software interrupts from user land).
  ///   - Ring 0 (OS)
  ///   - Ring 1 (e.g. Device Drivers)
  ///   - Ring 2 (e.g. Device Drivers)
  ///   - Ring 3 (User)
  /// Last (highest) bit is the present bit (P). Must be 1 for the descriptor to be valid.
  /// [Source: https://wiki.osdev.org/Interrupt_Descriptor_Table]
  @SJC(offset=4)
  public short metaData;

  /// High part of the address of the interrupt service routine
  @SJC(offset=6)
  public short offsetHigh;
}