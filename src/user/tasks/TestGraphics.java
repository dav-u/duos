package user.tasks;

import kernel.io.GraphicsBuffer;
import kernel.io.keyboard.KeyEvent;
import kernel.io.keyboard.KeyCode;
import kernel.io.keyboard.Keyboard;
import kernel.scheduler.GraphicsUiTask;
import math.Math;

import user.lib.audio.*;
import kernel.hardware.ac97.AC97;

public class TestGraphics extends GraphicsUiTask {
  /* Graphics */
  private GraphicsBuffer buffer = new GraphicsBuffer(1920, 1080);

  /* Sound */
  private final static int SAMPLES_TO_GENEREATE_PER_CYCLE = 980;
  private final static float BASE_FREQUENCY = 261.63f; // middle C
  private final static int MAX_CONCURRENT_FREQUENCIES = 5;

  private Waveform sawtoothWaveform;
  private Waveform squareWaveform;
  private Waveform sineWaveform;
  private Waveform nullWaveform;
  private Waveform activeWaveform;

  /* We have MAX_CONCURRENT_FREQUENCIES different tones we can play at the same time */
  private float[] frequencies = new float[MAX_CONCURRENT_FREQUENCIES];
  private Oscillator[] oscillators = new Oscillator[MAX_CONCURRENT_FREQUENCIES];
  private int[] frequencyIndexToKeyCode = new int[MAX_CONCURRENT_FREQUENCIES];

  private int previousSampleIndex = 0;

  /* Logic */
  private int[] keyCodeToKeyIndex = new int[Keyboard.keyCount];
  private boolean[] pressedKeys = new boolean[17];

  public TestGraphics() {
    for (int i = 0; i < MAX_CONCURRENT_FREQUENCIES; i++) {
      this.frequencies[i] = 0.0f;
      this.oscillators[i] = new Oscillator(this.nullWaveform, AC97.SAMPLE_RATE);
      this.oscillators[i].setVolume(0.3f);
    }

    for (int i = 0; i < Keyboard.keyCount; i++) {
      this.keyCodeToKeyIndex[i] = -1;
    }

    for (int i = 0; i < MAX_CONCURRENT_FREQUENCIES; i++) {
      this.frequencyIndexToKeyCode[i] = -1;
    }

    // map keyboard keys to half tone steps
    this.keyCodeToKeyIndex[KeyCode.A] = 0; // A -> 0 (C)
    this.keyCodeToKeyIndex[KeyCode.W] = 1;
    this.keyCodeToKeyIndex[KeyCode.S] = 2;
    this.keyCodeToKeyIndex[KeyCode.E] = 3;
    this.keyCodeToKeyIndex[KeyCode.D] = 4;
    this.keyCodeToKeyIndex[KeyCode.F] = 5;
    this.keyCodeToKeyIndex[KeyCode.T] = 6;
    this.keyCodeToKeyIndex[KeyCode.G] = 7;
    this.keyCodeToKeyIndex[KeyCode.Z] = 8;
    this.keyCodeToKeyIndex[KeyCode.H] = 9;
    this.keyCodeToKeyIndex[KeyCode.U] = 10;
    this.keyCodeToKeyIndex[KeyCode.J] = 11;
    this.keyCodeToKeyIndex[KeyCode.K] = 12;
    this.keyCodeToKeyIndex[KeyCode.O] = 13;
    this.keyCodeToKeyIndex[KeyCode.L] = 14;
    this.keyCodeToKeyIndex[KeyCode.P] = 15;
    this.keyCodeToKeyIndex[KeyCode.O_Umlaut] = 16;
    this.keyCodeToKeyIndex[KeyCode.A_Umlaut] = 17;

    this.sawtoothWaveform = new SawtoothWaveform();
    this.squareWaveform = new SquareWaveform();
    this.sineWaveform = new SineWaveform();
    this.nullWaveform = new NullWaveform();
    this.activeWaveform = this.sineWaveform;
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

  boolean initialized = false;

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
    int keyHeight = 200;
    int whiteKeyWidth = 50;
    int blackKeyWidth = whiteKeyWidth / 2;
    int keyTop = buffer.height - keyHeight - 5;
    int white = 0xFFFFFFFF;
    int whiteDown = 0xFFAAAAFF;
    int black = 0xFF000000;
    int blackDown = 0xFF0000AA;
    int gap = 2; // px

    for (int k = 0; k < 11; k++) {
      int offset = k*(whiteKeyWidth + gap);
      if (isDisplayKeyPressed(k, true))
        this.buffer.drawRect(offset, keyTop, whiteKeyWidth, keyHeight, whiteDown);
      else this.buffer.drawRect(offset, keyTop, whiteKeyWidth, keyHeight, white);
    }

    for (int k = 0; k < 10; k++) {
      if (k % 7 == 2 || k % 7 == 6) continue;
      int offset = k*(whiteKeyWidth + gap);
      if (isDisplayKeyPressed(k, false))
        this.buffer.drawRect(offset + whiteKeyWidth - blackKeyWidth/2, keyTop, blackKeyWidth, keyHeight/3*2, blackDown);
      else this.buffer.drawRect(offset + whiteKeyWidth - blackKeyWidth/2, keyTop, blackKeyWidth, keyHeight/3*2, black);
    }

    this.buffer.renderTo(this.Graphics);
  }

  private int displayIndexToKeyIndex(int displayIndex, boolean isWhiteKey) {
    int octave = displayIndex / 7;
    displayIndex %= 7;

    if (isWhiteKey) {
      if (displayIndex <= 2) return 12 * octave + displayIndex * 2;
      else return 12 * octave + displayIndex * 2 - 1;
    }
    else {
      if (displayIndex <= 2) return 12 * octave + displayIndex * 2 + 1;
      else return 12 * octave + displayIndex * 2;
    }
  }

  private boolean isDisplayKeyPressed(int displayIndex, boolean isWhiteKey) {
    int keyIndex = displayIndexToKeyIndex(displayIndex, isWhiteKey);

    return this.pressedKeys[keyIndex];
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