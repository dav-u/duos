package kernel.hardware.ac97;

public class BufferDescriptor extends STRUCT {
  /*
   * Physical Address to sound data in memory
   */
  @SJC(offset=0x0)
  public int soundPtr;

  /*
   * Number of samples in this buffer
   */
  @SJC(offset=0x4)
  public short sampleCount;

  /*
   * Bit 15=Interrupt fired when data from this entry is transferred Bit 14=Last entry of buffer, stop playing Other bits=Reserved
   */
  @SJC(offset=0x6)
  public short flags;
}