package kernel.screen;

/// This class provides basic output functionality
public class ScreenWriter {
  private byte color = PixelColor.DEFAULT;

  /// predefined methods
  public void println(char c) { print(c); println(); }
  public void println(int i) { print(i); println(); }
  public void println(long l) { print(l); println(); }
  public void println(String str) { print(str); println(); }

  public void setColor(byte fg, byte bg) {
    color = PixelHelper.setForeground(color, fg);
    color = PixelHelper.setBackground(color, bg);
  }

  public void setCursor(int newX, int newY) {
    if (newX >= Screen.width) return;
    if (newY >= Screen.height) return;

    Screen.cursorIndex = newY * Screen.width + newX;
  }

  public void print(char c) {
    Screen.print(c, color);
  }

  public void print(int x) {
    // a bit lazy :D
    print((long)x);
  }

  public void print(long x) {
    // this is not a very efficient solution but it works

    // max long is 9,223,372,036,854,775,807
    long digitRange = 1000000000000000000L;

    if (x < 0) {
      print('-');
      x = -x;
    }

    // make digitRange smaller than (or equal) to integer
    while (digitRange > x) digitRange /= 10;

    while (digitRange != 0) {
      long digit = x / digitRange;
      x -= digitRange * digit;
      char c = (char)(((byte)digit) + ((byte)'0'));

      print(c);

      digitRange /= 10;
    }
  }

  public void printHex(byte b) {
    String digits = "0123456789ABCDEF";

    // four bit make one hex digit 
    byte mask = 0xF; // 0b1111

    print(digits.charAt((b >>> 4) & mask));
    print(digits.charAt(b & mask));
  }

  public void printHex(short s) {
    short mask = 0xFF; //0b1111_1111

    printHex((byte)(s >>> 8));
    printHex((byte)(s & mask));
  }

  public void printHex(int x) {
    int mask = 0xFFFF;

    printHex((short)(x >>> 16));
    printHex((short)(x & mask));
  }

  public void printHex(long x) {
    long mask = 0xFFFFFFFFL;

    printHex((int)(x >>> 32));
    printHex((int)(x & mask));
  }

  public void print(String str) {
    Screen.print(str, color);
  }

  public void println() {
    Screen.print('\n', color);
  }
}