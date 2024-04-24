package kernel.io.console;

/// This class provides basic output functionality
public class ConsoleWriter {
  private byte color = SymbolColor.DEFAULT;

  /// predefined methods
  public void println(char c) { print(c); println(); }
  public void println(int i) { print(i); println(); }
  public void println(long l) { print(l); println(); }
  public void println(String str) { print(str); println(); }

  public void setColor(byte fg, byte bg) {
    color = SymbolColor.setForeground(color, fg);
    color = SymbolColor.setBackground(color, bg);
  }

  public void setCursor(int newX, int newY) {
    if (newX >= Console.width) return;
    if (newY >= Console.height) return;

    Console.cursorIndex = newY * Console.width + newX;
  }

  public void print(char c) {
    Console.print(c, color);
  }

  public void print(int x) {
    // a bit lazy :D
    Console.print(x, color);
  }

  public void print(long l) {
    Console.print(l, color);
  }

  public void printHex(byte b) {
    Console.printHex(b, color);
  }

  public void printHex(short s) {
    Console.printHex(s, color);
  }

  public void printHex(int x) {
    Console.printHex(x, color);
  }

  public void printHex(long x) {
    Console.printHex(x, color);
  }

  public void print(String str) {
    Console.print(str, color);
  }

  public void println() {
    Console.print('\n', color);
  }
}