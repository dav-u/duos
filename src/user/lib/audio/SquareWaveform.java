package user.lib.audio;

import math.Math;

public class SquareWaveform extends Waveform {
  public float getAmpByPhase(float phase) {
    if (phase < Math.PI) return (float)-1.0;
    else return (float)1.0;
  }
}