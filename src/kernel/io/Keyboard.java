package kernel.io;

import kernel.screen.PixelColor;
import kernel.screen.Screen;

public class Keyboard {
  public static void init() {
    KeyMap.init();
  }

  public static void readIoBuffer() {
    byte code = MAGIC.rIOs8(0x60);
    Screen.printHex(code, PixelColor.DEFAULT);
    Screen.print('\n');
  }

  private static boolean isMakeCode(byte code) {
    return (code & (1 << 7)) == 0;
  }

  private static boolean isBreakCode(byte code) {
    return (code & (1 << 7)) != 0;
  }

  private static byte clearBreakBit(byte code) {
    return (byte)(code & ~(1 << 7));
  }
}