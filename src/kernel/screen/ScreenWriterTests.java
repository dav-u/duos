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
    writer.setColor(PixelColor.BLUE, PixelColor.BLACK);
    // writer.setColor(PixelColor.RED, PixelColor.VIOLET);
    // writer.print("Hello World");
    // writer.setColor(PixelColor.DEFAULT, PixelColor.BLACK);

    ScreenBufferPixel pixel = Screen.buffer.pixels[3];
    return pixel.symbol == 'l';// && pixel.color == 
  }

  public boolean testInteger() {
    Screen.clear();
    writer.print(1234567);

    return checkScreenTextAt(0, "1234567");
  }

  public boolean testHex() {
    Screen.clear();
    writer.printHex((byte)0xEA);
    writer.println();
    writer.printHex((short)0xDEAD);
    writer.println();
    writer.printHex((int)0x1C0FFEE1);
    writer.println();
    writer.printHex((long)0x123456789098765L);

    return 
      checkScreenTextAt(Screen.width * 0, "EA")
      && checkScreenTextAt(Screen.width * 1, "DEAD")
      && checkScreenTextAt(Screen.width * 2, "1C0FFEE1")
      && checkScreenTextAt(Screen.width * 3, "0123456789098765");
  }

  private boolean checkScreenTextAt(int cursorIndex, String expectedString) {
    for (int i = 0; i < expectedString.length(); i++) {
      if (Screen.buffer.pixels[cursorIndex + i].symbol != expectedString.charAt(i))
        return false;
    }

    return true;
  }
}