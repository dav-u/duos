package kernel.scheduler;

import kernel.io.console.Console;
import kernel.io.keyboard.KeyEvent;

public class BaseTask extends Task {

  public boolean handleKeyEvent(KeyEvent event) {
    Console.print("Handled key event\n");
    return false;
  }

  public boolean run() {
    return false;
  }
}