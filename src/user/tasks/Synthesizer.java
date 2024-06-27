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
  private boolean isPlaying = false;
  private Oscillator oscillator;
  private Waveform activeWaveform;
  private Waveform sawtoothWaveform;
  private Waveform nullWaveform;

  private int previousSampleIndex = 0;
  private int writtenUpToSampleIndex = 0;

  public Synthesizer() {
    // Waveform wave = new SquareWaveform();
    // Waveform wave = new SawtoothWaveform();
    this.sawtoothWaveform = new SawtoothWaveform();
    this.nullWaveform = new NullWaveform();
    this.activeWaveform = this.nullWaveform;
    this.oscillator = new Oscillator(this.nullWaveform, AC97.SAMPLE_RATE);
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
      this.oscillator.waveform = this.sawtoothWaveform;
    }

    if (event.type == KeyEvent.Up && event.key.code == KeyCode.Enter) {
      // isPlaying = false;
      this.oscillator.waveform = this.nullWaveform;
    }

    return true;
  }

  @Override
  public boolean run() {
    // just for testing
    isPlaying = true;

    if (AC97.sampleIndex < this.previousSampleIndex)
      this.previousSampleIndex -= AC97.PCM_SCRATCH_SIZE;
    
    // elapsedSamples = AC97.sampleIndex - this.prthis.oscillator.readSample()eviousSampleIndex;
    // float elapsedSeconds = elapsedSamples / (float)AC97.SAMPLE_RATE;
    // this.oscillator.update(elapsedSeconds);

    // how much till the sound card catches up
    int indexDiff = this.writtenUpToSampleIndex - AC97.sampleIndex;
    // if (indexDiff < 20000) Console.print("O");
    if (indexDiff < 0) indexDiff += AC97.sampleIndex;

    int sampleCountToGenerate = 10; // TODO: make constant

    if (isPlaying && indexDiff < 20000) {
      this.oscillator.generate(sampleCountToGenerate, (float)440.0);
      for (int i = 0; i < sampleCountToGenerate; i++) {
        short sample = (short)(this.oscillator.readSample() * 30000); // TODO: 30000?
        AC97.writeSample(sample);
      }

      this.writtenUpToSampleIndex = AC97.sampleIndex + sampleCountToGenerate % AC97.PCM_SCRATCH_SIZE;
    }
    else {
      //Console.print("We are fast enought!");
    }

    this.previousSampleIndex = AC97.sampleIndex;
    this.writtenUpToSampleIndex = 10;

    return true;
  }
}