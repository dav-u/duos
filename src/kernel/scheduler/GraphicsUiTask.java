package kernel.scheduler;

import kernel.bios.BIOS;
import kernel.hardware.vesa.GraphicsDriver;
import kernel.hardware.vesa.VESAGraphics;
import kernel.io.console.Console;
import kernel.io.console.ConsoleSwitchBuffer;
import kernel.io.keyboard.KeyEvent;

public abstract class GraphicsUiTask extends UiTask {
  public VESAGraphics Graphics = VESAGraphics.detectDevice();

  @Override
  public void onActivate() {
    this.Graphics.setMode(1920, 1080, 32, true);
  }
}