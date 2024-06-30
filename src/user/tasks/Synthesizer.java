package user.tasks;

import kernel.hardware.ac97.AC97;
import kernel.scheduler.TextUiTask;
import user.lib.audio.Oscillator;
import user.lib.audio.SquareWaveform;
import user.lib.audio.SawtoothWaveform;
import user.lib.audio.NullWaveform;
import user.lib.audio.Waveform;
import kernel.io.keyboard.KeyCode;
import kernel.io.keyboard.KeyEvent;
import kernel.io.console.Console;

public class Synthesizer extends TextUiTask {
  private Oscillator oscillator;
  private Waveform sawtoothWaveform;
  private Waveform squareWaveform;
  private Waveform nullWaveform;

  private int previousSampleIndex = 0;

  private final static int SAMPLES_TO_GENEREATE_PER_CYCLE = 980;

  public Synthesizer() {
    this.sawtoothWaveform = new SawtoothWaveform();
    this.squareWaveform = new SquareWaveform();
    this.nullWaveform = new NullWaveform();
    this.oscillator = new Oscillator(this.nullWaveform, AC97.SAMPLE_RATE);
    this.oscillator.setVolume(0.3);
  }

  @Override
  public String getName() { return "Synthesizer"; }

  @Override
  public void onActivate() {
    super.onActivate();
    Console.clear();
    Console.print("Synthesizer: Let's play some tunes");
  }

  @Override
  public boolean handleKeyEventInternal(KeyEvent event) {
    if (event.type == KeyEvent.Down && event.key.code == KeyCode.Enter) {
      // isPlaying = true;
      this.oscillator.waveform = this.squareWaveform;
    }

    if (event.type == KeyEvent.Up && event.key.code == KeyCode.Enter) {
      // isPlaying = false;
      this.oscillator.waveform = this.nullWaveform;
    }

    return true;
  }

  boolean initialized = false;

  @Override
  public boolean run() {
    return true;
    if (!this.initialized) {
      AC97.setWriteIndex(AC97.sampleIndex);
      this.previousSampleIndex = AC97.sampleIndex;
      this.initialized = true;
    }

    // previous = 200, index = 300 -> 100 samples have passed
    // previous = 1000, index = 100 -> 300 (wrap around at 1200)
    int diff = AC97.sampleIndex - this.previousSampleIndex;
    if (diff < 0) diff = -diff;

    int sampleCountToGenerate = SAMPLES_TO_GENEREATE_PER_CYCLE;

    if (diff > SAMPLES_TO_GENEREATE_PER_CYCLE/2) {
      this.oscillator.generate(sampleCountToGenerate, (float)440.0);
      for (int i = 0; i < sampleCountToGenerate; i++) {
        short sample = (short)(this.oscillator.readSample() * 32767);
        AC97.writeSample(sample);
      }

      this.previousSampleIndex = AC97.sampleIndex;
    }

    return true;
  }
}