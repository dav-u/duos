package kernel.io.keyboard;

import kernel.io.console.SymbolColor;
import kernel.io.console.Console;

public class Keyboard {
  public final static int keyCount = 94;

  public static KeyBuffer keyBuffer;

  private static KeyMap keyMap;

  private static boolean e0 = false;
  private static int e1IgnoreCount = 0;

  private static boolean[] pressState;
  private static boolean[] e0PressState;

  private static boolean waitingOnKeyPress = false;
  private static int keyCodeWaitingOn = 0;
  
  public static void init() {
    keyMap = KeyMap.greateGermanKeymap();
    pressState = new boolean[keyCount];
    e0PressState = new boolean[keyCount];
    keyBuffer = new KeyBuffer(256);
  }

  /*
   * Inspired by http://www.lowlevel.eu/wiki/Keyboard_Controller.
   */
  public static void readIoBuffer() {
    int code = MAGIC.rIOs8(0x60);
    code &= 0xFF;

    // we ignore the next 2 bytes after e1
    if (e1IgnoreCount > 0) {
      e1IgnoreCount--;
      return;
    }

    // ignore diverse
    if (code >= 0xE2) return;

    // we remember seeing an e0 when reading the next byte
    if (code == 0xE0) {
      e0 = true;
      return;
    }
    
    if (code == 0xE1) {
      // ignore next 2 bytes
      e1IgnoreCount = 2;
      return;
    }

    if (e0) {
      // 0x2A is Fake LShift
      // 0x36 is Fake LShift
      // see https://www.win.tue.nl/~aeb/linux/kbd/scancodes-1.html#ss1.6
      // ignore fake shifts
      if (code == 0x2A || code == 0x36) {
        e0 = false;
        return;
      }

      // we create a new keyboard code by appending 0xe0 to the scancode.
      // KeyCode contains both variants xx and e0xx
      code = 0xe0 << 8 | code;
    }

    int normalizedCode = clearBreakBit(code);
    Key key = keyMap.getKeyBy(normalizedCode);
    if (key == null) return;

    boolean isKeyPressed = getPressState(normalizedCode);

    if (isKeyPressed && isBreakCode(code)) {
      setPressState(normalizedCode, false);
      keyUp(key);
      return;
    }

    if (isMakeCode(code) && !isKeyPressed) {
      setPressState(normalizedCode, true);
      keyDown(key);
    }

    if (isMakeCode(code)) {
      keyPress(key);
    }
  }

  /*
   * Pauses execution until the specified key is pressed down.
   * Useful for debugging.
   */
  public static void waitFor(int keyCode) {
    keyCodeWaitingOn = keyCode;
    waitingOnKeyPress = true;
    while (waitingOnKeyPress);
  }

  private static void keyUp(Key key) {
    keyBuffer.appendEvent(key, KeyEvent.Up);
  }

  private static void keyDown(Key key) {
    if (key.code == keyCodeWaitingOn) waitingOnKeyPress = false;

    keyBuffer.appendEvent(key, KeyEvent.Down);
  }

  private static void keyPress(Key key) {
    keyBuffer.appendEvent(key, KeyEvent.Press);
  }

  private static boolean getPressState(int code) {
    return KeyCode.isE0Code(code)
      ? e0PressState[KeyCode.removeEscape(code)]
      : pressState[code];
  }

  private static void setPressState(int code, boolean state) {
    if (KeyCode.isE0Code(code))
      e0PressState[KeyCode.removeEscape(code)] = state;
    else pressState[code] = state;
  }

  private static boolean isMakeCode(int code) {
    return (code & (1 << 7)) == 0;
  }

  private static boolean isBreakCode(int code) {
    return (code & (1 << 7)) != 0;
  }

  private static int clearBreakBit(int code) {
    return code & ~(1 << 7);
  }
}