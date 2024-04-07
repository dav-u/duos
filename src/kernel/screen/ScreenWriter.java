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
    Screen.print(x, color);
  }

  public void print(long l) {
    Screen.print(l, color);
  }

  public void printHex(byte b) {
    Screen.printHex(b, color);
  }

  public void printHex(short s) {
    Screen.printHex(s, color);
  }

  public void printHex(int x) {
    Screen.printHex(x, color);
  }

  public void printHex(long x) {
    Screen.printHex(x, color);
  }

  public void print(String str) {
    Screen.print(str, color);
  }

  public void println() {
    Screen.print('\n', color);
  }
}