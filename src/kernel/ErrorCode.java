package kernel;

public class ErrorCode {
  public final static int DivisionByZero = 0x1;
  public final static int Nmi = 0x2;
  public final static int InvalidOpcode = 0x3;
  public final static int BiosCallFailed = 0x4;
  public final static int RestoreInterruptsCalledTooOften = 0x5;
  public final static int InvalidTaskPriority = 0x6;
  public final static int OutOfMemory = 0x7;
  public final static int VirtualMemory = 0x8;
}