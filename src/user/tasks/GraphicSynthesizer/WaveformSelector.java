package user.tasks.GraphicSynthesizer;

import kernel.io.keyboard.KeyCode;
import kernel.io.keyboard.KeyEvent;
import user.lib.audio.*;

public class WaveformSelector {
  private Waveform sawtoothWaveform = new SawtoothWaveform();
  private Waveform squareWaveform = new SquareWaveform();
  private Waveform sineWaveform = new SineWaveform();
  private Waveform triangleWaveform = new TriangleWaveform();

  private Waveform selectedWaveform = sineWaveform;

  public Waveform getSelectedWaveform() {
    return this.selectedWaveform;
  }

  public boolean handleKeyEvent(KeyEvent event) {
    if (event.key.code != KeyCode.Left) return false;

    return true;
  }
}