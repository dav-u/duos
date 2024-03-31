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

  public static void printHex(byte b) {
    String digits = "0123456789ABCDEF";

    // four bit make one hex digit 
    byte mask = 0xF; // 0b1111

    print(digits.charAt((b >>> 4) & mask));
    print(digits.charAt(b & mask));
  }

  public static void printHex(short s) {
    short mask = 0xFF; //0b1111_1111

    printHex((byte)(s >>> 8));
    printHex((byte)(s & mask));
  }

  public static void printHex(int x) {
    int mask = 0xFFFF;

    printHex((short)(x >>> 16));
    printHex((short)(x & mask));
  }
}