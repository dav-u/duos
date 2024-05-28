package user.tasks;

import kernel.io.console.Console;
import kernel.io.keyboard.KeyCode;
import kernel.io.keyboard.KeyEvent;
import kernel.scheduler.TextUiTask;

public class BreakpointTask extends TextUiTask {
  @Override
  public String getName() { return "BreakpointTask"; }

  @Override
  public void onActivate() {
    super.onActivate();
    Console.clear();
    Console.print("Press enter to trigger a breakpoint");
  }

  @Override
  protected boolean handleKeyEventInternal(KeyEvent event) {
    if (event.key.code == KeyCode.Enter)
      MAGIC.inline(0xcc);
    return true;
  }
}