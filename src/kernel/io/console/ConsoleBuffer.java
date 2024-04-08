package kernel.io.console;

public class ConsoleBuffer extends STRUCT {
  @SJC(count=2000)
  public ConsoleBufferPixel[] pixels;
}