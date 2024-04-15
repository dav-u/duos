package kernel.io.keyboard;

import kernel.interrupt.Interrupts;
import kernel.io.console.Console;
import kernel.io.console.SymbolColor;

public class KeyboardTextInterpreter {
  private KeyBufferReader keyBufferReader;

  private boolean isLeftShiftDown = false;
  private boolean isRightShiftDown = false;

  public KeyboardTextInterpreter(KeyBufferReader keyBufferReader) {
    this.keyBufferReader = keyBufferReader;
  }

  public void execute() {
    // make sure that no new events come in while we work on the buffer
    // Interrupts.clearInterruptFlag();

    KeyEvent event = keyBufferReader.readNext();
    if (event == null) return;

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
      Console.print(c);

    // Interrupts.setInterruptFlag();
  }
}