package kernel.scheduler;

import kernel.io.console.Console;
import kernel.io.console.ConsoleSwitchBuffer;
import kernel.io.keyboard.KeyEvent;

public abstract class TextUiTask extends UiTask {
  protected ConsoleSwitchBuffer switchBuffer = new ConsoleSwitchBuffer();

  @Override
  public void onActivate() {
    Console.switchBufferTo(switchBuffer);
  }
}