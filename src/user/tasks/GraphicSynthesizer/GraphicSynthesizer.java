package user.tasks.GraphicSynthesizer;

import kernel.io.GraphicsBuffer;
import kernel.io.keyboard.KeyEvent;
import kernel.io.keyboard.KeyCode;
import kernel.io.keyboard.Keyboard;
import kernel.scheduler.GraphicsUiTask;
import math.Math;

import user.lib.audio.*;
import kernel.hardware.ac97.AC97;

public class GraphicSynthesizer extends GraphicsUiTask {
  /* Graphics */
  private GraphicsBuffer buffer;
  private KeyboardDisplay keyboardDisplay;
  private WaveformDisplay waveformDisplay;

  /* Sound */
  private final static int SAMPLES_TO_GENEREATE_PER_CYCLE = 980;
  private final static float BASE_FREQUENCY = 261.63f; // middle C
  private final static int MAX_CONCURRENT_FREQUENCIES = 5;

  private Waveform activeWaveform;
  private Waveform sawtoothWaveform = new SawtoothWaveform();
  private Waveform squareWaveform = new SquareWaveform();
  private Waveform sineWaveform = new SineWaveform();
  private Waveform triangleWaveform = new TriangleWaveform();
  private Waveform nullWaveform = new NullWaveform();

  /* We have MAX_CONCURRENT_FREQUENCIES different tones we can play at the same time */
  private float[] frequencies = new float[MAX_CONCURRENT_FREQUENCIES];
  private Oscillator[] oscillators = new Oscillator[MAX_CONCURRENT_FREQUENCIES];
  private int[] frequencyIndexToKeyCode = new int[MAX_CONCURRENT_FREQUENCIES];

  private int previousSampleIndex = 0;

  /* Logic */
  private int[] keyCodeToKeyIndex = new int[Keyboard.keyCount];
  private boolean[] pressedKeys = new boolean[17];
  private boolean initialized = false;

  public GraphicSynthesizer() {
    for (int i = 0; i < MAX_CONCURRENT_FREQUENCIES; i++) {
      this.frequencies[i] = 0.0f;
      this.oscillators[i] = new Oscillator(this.nullWaveform, AC97.SAMPLE_RATE);
      this.oscillators[i].setVolume(0.2f);
    }

    for (int i = 0; i < Keyboard.keyCount; i++) {
      this.keyCodeToKeyIndex[i] = -1;
    }

    KeyboardLayouts.setDefaultLayout(this.keyCodeToKeyIndex);

    for (int i = 0; i < MAX_CONCURRENT_FREQUENCIES; i++) {
      this.frequencyIndexToKeyCode[i] = -1;
    }

    this.activeWaveform = this.squareWaveform;

    this.buffer = new GraphicsBuffer(1920, 1080);
    this.keyboardDisplay = new KeyboardDisplay(800);
    this.waveformDisplay = new WaveformDisplay(this.activeWaveform);
  }

  @Override
  public String getName() { return "Graphic Synthesizer"; }

  @Override
  public void onActivate() {
    super.onActivate();
  }

  @Override
  public boolean handleKeyEventInternal(KeyEvent event) {
    if (event.type == KeyEvent.Down) {
      int keyIndex = this.keyCodeToKeyIndex[event.key.code];
      if (keyIndex == -1) return true;

      int freqIndex = nextFreeFrequencyIndex();
      if (freqIndex == -1) return true;

      this.frequencies[freqIndex] = (float)(BASE_FREQUENCY * Math.pow(2, keyIndex/12.0f));
      this.oscillators[freqIndex].waveform = this.activeWaveform;
      this.frequencyIndexToKeyCode[freqIndex] = event.key.code;

      this.pressedKeys[keyIndex] = true;
    }

    if (event.type == KeyEvent.Up) {
      int keyIndex = this.keyCodeToKeyIndex[event.key.code];
      if (keyIndex == -1) return true;

      int freqIndex = getFrequencyIndexOfKeyCode(event.key.code);
      if (freqIndex == -1) return true; // should not happen

      this.frequencies[freqIndex] = 0.0f;
      this.oscillators[freqIndex].waveform = this.nullWaveform;
      this.frequencyIndexToKeyCode[freqIndex] = -1;

      this.pressedKeys[keyIndex] = false;
    }

    return true;
  }

  @Override
  public boolean run() {
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
      for (int i = 0; i < MAX_CONCURRENT_FREQUENCIES; i++) {
        if (this.frequencyIndexToKeyCode[i] == -1) continue;

        this.oscillators[i].generate(sampleCountToGenerate, this.frequencies[i]);
      }

      for (int i = 0; i < sampleCountToGenerate; i++) {
        short sample = 0;
        for (int j = 0; j < MAX_CONCURRENT_FREQUENCIES; j++) {
          if (this.frequencyIndexToKeyCode[j] == -1) continue;

          sample += (short)(this.oscillators[j].readSample() * 32767);
        }
        AC97.writeSample(sample);
      }

      this.previousSampleIndex = AC97.sampleIndex;
    }

    return true;
  }

  @Override
  public void display() {
    this.keyboardDisplay.drawTo(this.buffer, this.pressedKeys);
    this.waveformDisplay.drawTo(buffer, 10, 10, 300, 200);
    this.buffer.renderTo(this.Graphics);
  }

  private int nextFreeFrequencyIndex() {
    for (int i = 0; i < MAX_CONCURRENT_FREQUENCIES; i++) {
      if (this.frequencyIndexToKeyCode[i] == -1) return i;
    }

    return -1;
  }

  private int getFrequencyIndexOfKeyCode(int keyCode) {
    for (int i = 0; i < MAX_CONCURRENT_FREQUENCIES; i++) {
      if (this.frequencyIndexToKeyCode[i] == keyCode) return i;
    }

    return -1;
  }
}