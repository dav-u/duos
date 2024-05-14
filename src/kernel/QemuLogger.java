package kernel;

import rte.instancing.GarbageCollectingInstanceCreator;

public class QemuLogger {
  public static void printDynamicObjects() {
    Object current = GarbageCollectingInstanceCreator.firstDynamicObject;
    while (current != null) {
      print(current._r_type.name);
      print("\n");
      current = current._r_next;
    }
  }

  public static void printChar(char c) {
    MAGIC.wIOs8(0x3F8, (byte)c);
  }

  public static void print(String s) {
    for (int i = 0; i < s.length(); i++)
      printChar(s.charAt(i));
  }

  public static void printHex(byte b) {
    String digits = "0123456789ABCDEF";

    // four bit make one hex digit 
    byte mask = 0xF; // 0b1111

    printChar(digits.charAt((b >>> 4) & mask));
    printChar(digits.charAt(b & mask));
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
