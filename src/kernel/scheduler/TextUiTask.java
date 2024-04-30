package kernel.scheduler;

import kernel.io.console.Console;
import kernel.io.console.ConsoleSwitchBuffer;
import kernel.io.keyboard.KeyEvent;

public abstract class TextUiTask extends UiTask {
  private ConsoleSwitchBuffer switchBuffer = new ConsoleSwitchBuffer();

  @Override
  public void onActivate() {
    Console.switchBufferTo(switchBuffer);
  }
}