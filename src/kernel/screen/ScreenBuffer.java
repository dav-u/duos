package kernel.screen;

public class ScreenBuffer extends STRUCT {
  @SJC(count=2000)
  public ScreenBufferPixel[] pixels;
}