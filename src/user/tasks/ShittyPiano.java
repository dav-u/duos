package user.tasks;

import kernel.hardware.PCI;
import kernel.hardware.PcSpeaker;
import kernel.scheduler.TextUiTask;
import kernel.io.console.Console;
import kernel.io.console.ConsoleSwitchBuffer;
import kernel.io.keyboard.KeyCode;
import kernel.io.keyboard.KeyEvent;

public class ShittyPiano extends TextUiTask {
  private static int[] keyCodes = {
    KeyCode.A,
    KeyCode.W,
    KeyCode.S,
    KeyCode.D,
    KeyCode.R,
    KeyCode.F,
    KeyCode.T,
    KeyCode.G,
    KeyCode.H,
    KeyCode.U,
    KeyCode.J,
    KeyCode.I,
    KeyCode.K,
    KeyCode.O,
    KeyCode.L,
    KeyCode.O_Umlaut,
    KeyCode.U_Umlaut,
    KeyCode.A_Umlaut,
  };

  private static int[] frequencies = {
    440,
    466,
    493,
    523,
    554,
    587,
    622,
    659,
    698,
    739,
    783,
    830,
    880,
    932,
    987,
    1046,
    1108,
    1174
  };

  private int pressedKeyCode = -1;

  @Override
  public String getName() {
    return "ShittyPiano (PC Speaker)";
  }

  @Override
  public void onActivate() {
    super.onActivate();
    Console.clear();
    Console.print("Let's play some tunes");
  }

  @Override
  protected boolean handleKeyEventInternal(KeyEvent event) {
    if (event.type == KeyEvent.Up && event.key.code == pressedKeyCode) {
      PcSpeaker.stop();
      return true;
    }

    if (event.type == KeyEvent.Down) {
      int keyCode = event.key.code;
      int index = 0;
      for (; index < keyCodes.length; index++) {
        if (keyCodes[index] == keyCode) break;
      }

      if (index == keyCodes.length) return false;

      // index was pressed

      int frequency = frequencies[index];

      PcSpeaker.playSound(frequency);
      pressedKeyCode = keyCode;
    }
    return false;
  }
}