
package user.tasks;

import kernel.bios.SystemMemoryMap;
import kernel.hardware.PCI;
import kernel.scheduler.TextUiTask;
import kernel.io.console.Console;
import kernel.io.console.ConsoleSwitchBuffer;
import kernel.io.keyboard.KeyEvent;

public class PrintMemoryMapTask extends TextUiTask {
  @Override
  public String getName() {
    return "PrintPci";
  }

  @Override
  public void onActivate() {
    Console.clear();
    SystemMemoryMap.printSystemMemoryMap();
  }
}