package kernel;

import kernel.io.keyboard.Shortcut;
import kernel.io.keyboard.KeyCode;
import kernel.io.console.Console;

public class BreakpointShortcut extends Shortcut {
  public BreakpointShortcut() {
    /// Ctrl + Alt + C
    this.Ctrl = true;
    this.Alt = true;
    this.keyCode = KeyCode.C;
  }

  public void execute() {
    MAGIC.inline(0xcc);
  }
}