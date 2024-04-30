package kernel.scheduler;

import kernel.io.console.Console;
import kernel.io.console.ConsoleSwitchBuffer;
import kernel.io.keyboard.KeyEvent;

public abstract class GraphicsUiTask extends Task {
  public GraphicsUiTask(int xResolution, int yResolution) { }

  @Override
  public void onActivate() {
    // switch buffer
  }
}