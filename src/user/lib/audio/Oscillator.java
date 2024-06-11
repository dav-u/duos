package user.lib.audio;

import math.Math;
import kernel.io.console.Console;

public class Oscillator {
  public final static int RING_BUFFER_SIZE = 256;
  public final int sampleRate;
  public final double secondsPerSample;

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
      ringBuffer[nextWriteIndex] = this.waveform.getAmpByPhase((float)this.phase);
      this.phase += secondsPerSample*880; // TODO: frequency
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
}