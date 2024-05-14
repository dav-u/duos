package user.tasks;

import kernel.io.console.Console;
import kernel.io.keyboard.KeyCode;
import kernel.io.keyboard.KeyEvent;
import kernel.scheduler.TextUiTask;

class WriteToNull extends TextUiTask {
  @Override
  public String getName() {
    return "WriteToNull";
  }
  
  @Override
  public boolean handleKeyEventInternal(KeyEvent event) {
    if (event.key.code == KeyCode.Enter && event.type == KeyEvent.Down) {
      Console.print("Trying to write to null pointer...\n");
      // write "random" constant to 0
      MAGIC.wMem32(0, 42069);
    }

    return false;
  }
}