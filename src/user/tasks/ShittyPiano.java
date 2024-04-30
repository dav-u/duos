package user.tasks;

import kernel.hardware.PCI;
import kernel.scheduler.TextUiTask;
import kernel.io.console.Console;
import kernel.io.console.ConsoleSwitchBuffer;
import kernel.io.keyboard.KeyEvent;

public class ShittyPiano extends TextUiTask {
  @Override
  public String getName() {
    return "ShittyPiano (PC Speaker)";
  }

  @Override
  public void onActivate() {
    Console.clear();
    PCI.printDevices();
  }

  @Override
  protected boolean handleKeyEventInternal(KeyEvent event) {
    return false;
  }
}