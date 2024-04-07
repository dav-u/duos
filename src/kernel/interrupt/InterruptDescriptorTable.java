package kernel.interrupt;

public class InterruptDescriptorTable extends STRUCT {
  @SJC(count=256)
  public InterruptDescriptor[] entries;
}