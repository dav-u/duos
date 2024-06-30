package user.lib.audio;

import math.Math;

public class SawtoothWaveform extends Waveform {
  public float getAmpByPhase(float phase) {
    if (phase < Math.PI) {
      return (float)(phase / Math.PI);
    }
    else return (float)(phase / Math.PI - 2.0);
  }
}