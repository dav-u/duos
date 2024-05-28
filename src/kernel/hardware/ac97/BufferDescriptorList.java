package kernel.hardware.ac97;

public class BufferDescriptorList extends STRUCT {
  @SJC(count=32)
  public BufferDescriptor[] descriptors;
}