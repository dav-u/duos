package user.lib.audio;

import math.Math;

public class SineWaveform extends Waveform {
  public float getAmpByPhase(float phase) {
    return (float)Math.sin(phase);
  }
}