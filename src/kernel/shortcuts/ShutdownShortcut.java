package kernel.shortcuts;

import kernel.io.keyboard.Shortcut;
import kernel.io.keyboard.KeyCode;
import kernel.Kernel;
import kernel.io.console.Console;

public class ShutdownShortcut extends Shortcut {
  public ShutdownShortcut() {
    // Ctrl + Alt + C
    this.Ctrl = true;
    this.Shift = true;
    this.Alt = true;
    this.keyCode = KeyCode.S;
  }

  public void execute() {
    Kernel.shutdown();
  }
}