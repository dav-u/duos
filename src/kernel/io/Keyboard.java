package kernel.io;

import kernel.io.console.SymbolColor;
import kernel.io.console.Console;

public class Keyboard {
  public final static int keyCount = 94;
  private static KeyMap keyMap;

  private static boolean[] pressState;

  public static void init() {
    keyMap = KeyMap.greateGermanKeymap();
    pressState = new boolean[keyCount];
  }

  public static void readIoBuffer() {
    byte code = MAGIC.rIOs8(0x60);

    // TODO: check for E0 or E1
    if (code == 0xE0 || code == 0xE1) return;

    byte normalizedCode = clearBreakBit(code);
    Key key = keyMap.getKeyBy(normalizedCode);
    if (key == null) return;

    boolean isKeyPressed = pressState[normalizedCode];

    if (isKeyPressed && isBreakCode(code)) {
      pressState[normalizedCode] = false;
      keyUp(key);
      return;
    }

    if (isMakeCode(code)) {
      keyPress(key);
    }

    if (isMakeCode(code) && !isKeyPressed) {
      pressState[normalizedCode] = true;
      keyDown(key);
      return;
    }

    // Console.printHex(code, SymbolColor.DEFAULT);
    // Console.print('\n');
  }

  private static void keyUp(Key key) {
    // Console.print("UP: ");
    // Console.print(key.name);
    // Console.print('\n');
  }

  private static void keyDown(Key key) {
    // Console.print("DOWN: ");
    // Console.print(key.name);
    // Console.print('\n');
  }

  private static void keyPress(Key key) {
    if (key.character == '\0')
      return;

    if (pressState[KeyboardCode.LShift] || pressState[KeyboardCode.RShift])
      Console.print(key.shiftCharacter);
    else Console.print(key.character);
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