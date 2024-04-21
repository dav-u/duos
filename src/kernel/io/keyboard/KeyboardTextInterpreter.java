package kernel.io.keyboard;

import kernel.interrupt.Interrupts;
import kernel.io.console.Console;
import kernel.io.console.SymbolColor;

/*
 * Converts KeyEvents to text.
 * After each handleKeyEvent getChar needs to be called.
 */
public class KeyboardTextInterpreter {
  private boolean isLeftShiftDown = false;
  private boolean isRightShiftDown = false;

  private char nextChar = '\0';

  /*
   * Returns the next char or '\0' if no
   * next char is available.
   */
  public char getChar() {
    char c = nextChar;
    nextChar = '\0';

    return c;
  }

  public void handleKeyEvent(KeyEvent event) {
    if (event.key.code == KeyCode.LShift) {
      if (event.type == KeyEvent.Down) isLeftShiftDown = true;
      else if (event.type == KeyEvent.Up) isLeftShiftDown = false;
    }

    if (event.key.code == KeyCode.RShift) {
      if (event.type == KeyEvent.Down) isRightShiftDown = true;
      else if (event.type == KeyEvent.Up) isRightShiftDown = false;
    }

    boolean isShiftDown = isLeftShiftDown || isRightShiftDown;

    char c = isShiftDown ? event.key.shiftCharacter : event.key.character;

    if (c != '\0' && event.type == KeyEvent.Press)
      nextChar = c;
  }
}