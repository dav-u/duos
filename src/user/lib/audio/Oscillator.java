package user.lib.audio;

import math.Math;
import kernel.io.console.Console;

public class Oscillator {
  public final static int RING_BUFFER_SIZE = 256;
  public final int sampleRate;
  public final double secondsPerSample;

  /*
   * Value between 0.0 and 1.0
   */
  public double volume = 1.0;
  public double phase;

  public Waveform waveform;
  public float[] ringBuffer;
  public int nextWriteIndex = 0;
  public int readIndex = 0;

  public Oscillator(Waveform waveform, int sampleRate) {
    this.waveform = waveform;
    this.ringBuffer = new float[RING_BUFFER_SIZE];
    this.sampleRate = sampleRate;
    this.secondsPerSample = 1.0/this.sampleRate;
  }

  public void generate(int count, float frequency) {
    this.readIndex = this.nextWriteIndex;
    for (int i = 0; i < count; i++) {
      ringBuffer[nextWriteIndex] = this.waveform.getAmpByPhase((float)this.phase) * (float)this.volume;
      this.phase += secondsPerSample*frequency*2.0f; // * 2.0 because 2 channels
      if (this.phase >= Math.TAU) this.phase = 0;
      //Console.print((int)(this.phase * 10000));

      nextWriteIndex++;
      nextWriteIndex %= RING_BUFFER_SIZE;
    }
  }

  public float readSample() {
    float sample = this.ringBuffer[this.readIndex];
    this.readIndex++;
    this.readIndex %= RING_BUFFER_SIZE;

    return sample;
  }

  public void setVolume(double volume) {
    if (volume < 0.0) this.volume = 0.0;
    else if (volume > 1.0) this.volume = 1.0;
    else this.volume = volume;
  }

  public double getVolume() {
    return this.volume;
  }
}