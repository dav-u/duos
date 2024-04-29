package kernel.io.console;

public class Console {
  private static final int screenMemoryAddress = 0xB8000;
  public static ConsoleBuffer directBuffer = (ConsoleBuffer)MAGIC.cast2Struct(screenMemoryAddress);

  public static final int size = 2000;
  public static final int width = 80;
  public static final int height = 25;

  public static int cursorIndex = 0;
  public static int indent = 0;

  public static ConsoleSwitchBuffer switchBuffer = null;

  public static void switchBufferTo(ConsoleSwitchBuffer newSwitchBuffer) {
    if (switchBuffer != null) {
      switchBuffer.cursorIndex = cursorIndex;
      switchBuffer.indent = indent;
    }

    switchBuffer = newSwitchBuffer;

    // ConsoleBufferPixel p = newSwitchBuffer.at(0);
    // Console.printHex(MAGIC.cast2Ref(p), (byte)7);
    printBuffer(newSwitchBuffer);

    cursorIndex = switchBuffer.cursorIndex;
    indent = switchBuffer.indent;
  }

  public static void printBuffer(ConsoleSwitchBuffer switchBuffer) {
    for (int i = 0; i < Console.size; i++) {
      directBuffer.pixels[i].color = switchBuffer.at(i).color;
      directBuffer.pixels[i].symbol = switchBuffer.at(i).symbol;
    }
  }

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
    print(c, (byte)-1);
  }

  public static void print(char c, byte color) {
    if (c == '\n') {
      linefeed();
      return;
    }
    if (c == '\b') {
      backspace();
      return;
    }

    directBuffer.pixels[cursorIndex].symbol = (byte)c;
    if (switchBuffer != null)
      switchBuffer.at(cursorIndex).symbol = (byte)c;

    if (color != -1) {
      directBuffer.pixels[cursorIndex].color = color;
      if (switchBuffer != null)
        switchBuffer.at(cursorIndex).color = color;
    }

    cursorIndex++;

    cursorIndex %= size; // wrap around
  }

  public static void backspace() {
    if (cursorIndex == 0) return;

    cursorIndex--;
    print(' ');
    cursorIndex--;
  }

  public static void linefeed() {
    int currentLine = cursorIndex / width;
    currentLine++;
    currentLine %= height;

    cursorIndex = currentLine * width + indent;
  }
  
  public static void clear() {
    cursorIndex = 0;
    for (int i = 0; i < size; i++)
      print(' ', SymbolColor.DEFAULT);
    cursorIndex = 0;
  }

  public static void printHex(byte b, byte color) {
    String digits = "0123456789ABCDEF";

    // four bit make one hex digit 
    byte mask = 0xF; // 0b1111

    print(digits.charAt((b >>> 4) & mask), color);
    print(digits.charAt(b & mask), color);
  }

  public static void printHex(short s, byte color) {
    short mask = 0xFF; //0b1111_1111

    printHex((byte)(s >>> 8), color);
    printHex((byte)(s & mask), color);
  }

  public static void printHex(int x, byte color) {
    int mask = 0xFFFF;

    printHex((short)(x >>> 16), color);
    printHex((short)(x & mask), color);
  }

  public static void printHex(long x, byte color) {
    long mask = 0xFFFFFFFFL;

    printHex((int)(x >>> 32), color);
    printHex((int)(x & mask), color);
  }

  public static void print(long x, byte color) {
    // this is not a very efficient solution but it works

    // max long is 9,223,372,036,854,775,807
    long digitRange = 1000000000000000000L;

    if (x == 0) {
      print('0', color);
      return;
    }

    if (x < 0) {
      print('-', color);
      x = -x;
    }

    // make digitRange smaller than (or equal) to integer
    while (digitRange > x) digitRange /= 10;

    while (digitRange != 0) {
      long digit = x / digitRange;
      x -= digitRange * digit;
      char c = (char)(((byte)digit) + ((byte)'0'));

      print(c, color);

      digitRange /= 10;
    }
  }
}