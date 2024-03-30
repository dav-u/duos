package kernel.screen;

/// Provides methods that test the ScreenWriter
/// Validation has to be done by developer and is not automated.
public class ScreenWriterTests {
  public ScreenWriter writer;

  public ScreenWriterTests() {
    writer = new ScreenWriter();
  }

  public boolean testWrap() {
    Screen.clear();
    for (int i = 0; i < 1900; i++) {
      writer.print('A');
    }

    for (int i = 0; i < 200; i++) {
      writer.print('B');
    }

    return Screen.buffer.pixels[0].symbol == 'B';
  }

  public boolean testColor() {
    Screen.clear();
    writer.setColor(PixelColor.BLUE, PixelColor.GRAY);
    // writer.setColor(PixelColor.RED, PixelColor.VIOLET);
    writer.print("Hello World");
    writer.setColor(PixelColor.DEFAULT, PixelColor.BLACK);

    ScreenBufferPixel pixel = Screen.buffer.pixels[3];
    return pixel.symbol == 'l';// && pixel.color == 
  }
}