package user.tasks;

import kernel.hardware.ac97.AC97;
import kernel.scheduler.TextUiTask;
import user.lib.audio.Oscillator;
import user.lib.audio.SquareWaveform;
import user.lib.audio.SawtoothWaveform;
import user.lib.audio.Waveform;
import kernel.io.keyboard.KeyCode;
import kernel.io.keyboard.KeyEvent;
import kernel.io.console.Console;

public class Synthesizer extends TextUiTask {
  private boolean isPlaying = false;
  private Oscillator oscillator;

  private int previousSampleIndex = 0;
  private int writtenUpToSampleIndex = 0;

  public Synthesizer() {
    Waveform wave = new SquareWaveform();
    // Waveform wave = new SawtoothWaveform();
    this.oscillator = new Oscillator(wave, AC97.SAMPLE_RATE);
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
      isPlaying = true;
      // fillSamplesWithSquare(AC97.pcmScratch, AC97.sampleIndex, AC97.SAMPLES_PER_BUFFER_DESC);
    }

    if (event.type == KeyEvent.Up && event.key.code == KeyCode.Enter) {
      isPlaying = false;
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

    int indexDiff = this.writtenUpToSampleIndex - AC97.sampleIndex;
    if (indexDiff < 0) indexDiff += AC97.sampleIndex;

    int sampleCountToGenerate = 200; // TODO: make constant

    if (isPlaying && indexDiff < 50) {
      this.oscillator.generate(sampleCountToGenerate, (float)440.0);
      for (int i = 0; i < sampleCountToGenerate; i++) {
        short sample = (short)(this.oscillator.readSample() * 30000); // TODO: 30000?
        AC97.writeSample(sample);
      }

      this.writtenUpToSampleIndex = AC97.sampleIndex + sampleCountToGenerate % AC97.PCM_SCRATCH_SIZE;
    }

    this.previousSampleIndex = AC97.sampleIndex;
    this.writtenUpToSampleIndex = 10;

    return true;
  }

  public static void fillSamplesWithSquare(short[] samples, int startIndex, int count) {
    int freq = 440; //Hz
    int samplesPerWave = AC97.SAMPLE_RATE / freq;
    boolean highFrequency = false;

    for (int i = 0; i < count; i++) {
      if (i % samplesPerWave == 0) highFrequency = !highFrequency;

      samples[i + startIndex] = highFrequency ? (short)0xF000 : (short)0;
    }
  }
}