package user.tasks;

import kernel.hardware.PCI;
import kernel.scheduler.TextUiTask;
import kernel.io.console.Console;
import kernel.io.console.ConsoleSwitchBuffer;
import kernel.io.keyboard.KeyEvent;

public class PrintPciTask extends TextUiTask {
  @Override
  public String getName() {
    return "PrintPci";
  }

  @Override
  public void onActivate() {
    super.onActivate();
    Console.clear();
    PCI.printDevices();
  }
}