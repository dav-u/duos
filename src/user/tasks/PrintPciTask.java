package user.tasks;

import kernel.hardware.PCI;
import kernel.scheduler.UiTask;
import kernel.io.console.Console;
import kernel.io.console.ConsoleSwitchBuffer;
import kernel.io.keyboard.KeyEvent;

public class PrintPciTask extends UiTask {
  ConsoleSwitchBuffer switchBuffer = new ConsoleSwitchBuffer();

  @Override
  public String getName() {
    return "PrintPci";
  }

  @Override
  public void display() { }

  @Override
  public void onActivate() {
    Console.switchBufferTo(switchBuffer);
    Console.clear();
    PCI.printDevices();
  }

  @Override
  public boolean handleKeyEventInternal(KeyEvent event) {
    return false;
  }
  
  @Override
  public boolean run() {
    return true;
  }
}