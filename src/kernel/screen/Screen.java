package kernel.screen;

public class Screen {
  // Screen
  private static final int screenMemoryAddress = 0xB8000;
  public static ScreenBuffer buffer = (ScreenBuffer)MAGIC.cast2Struct(screenMemoryAddress);

  public static final int size = 2000;
  public static final int width = 80;
  public static final int height = 25;

  public static int cursorIndex = 0;
  public static int indent = 0;

  public static void print(String str, byte color) {
    for (int i = 0; i < str.length(); i++) {
      print(str.charAt(i), color);
    }
  }

  public static void print(String str) {
    for (int i = 0; i < str.length(); i++) {
      print(str.charAt(i));
    }
  }

  public static void print(char c) {
    if (c == '\n') {
      linefeed();
      return;
    }

    buffer.pixels[cursorIndex++].symbol = (byte)c;
  }

  public static void print(char c, byte color) {
    if (c == '\n') {
      linefeed();
      return;
    }

    buffer.pixels[cursorIndex].color = color;
    buffer.pixels[cursorIndex++].symbol = (byte)c;

    cursorIndex %= size; // wrap around
  }

  public static void linefeed() {
    int currentLine = cursorIndex / width;
    cursorIndex = (currentLine+1) * width + indent;
  }
  
  public static void clear() {
    cursorIndex = 0;
    for (int i = 0; i < size; i++)
      print(' ', PixelColor.DEFAULT);
    cursorIndex = 0;
  }

  public static void printInteger(int integer) {
    // max integer is 2,147,483,647
    int digitRange = 1000000000;

    // make digitRange smaller than (or equal) to integer
    while (digitRange > integer) digitRange /= 10;

    while (digitRange != 0) {
      int digit = integer / digitRange;
      integer -= digitRange * digit;
      char c = (char)(((byte)digit) + ((byte)'0'));

      Screen.print(c);

      digitRange /= 10;
    }
  }
}