package user.lib.audio;

import math.Math;

public class TriangleWaveform extends Waveform {
  private static final float HALF_PI = (float)(Math.PI / 2.0);

  public float getAmpByPhase(float phase) {
    // return (float)(-2.0*phase / Math.PI*0.5 + 1.0);
    if (phase < HALF_PI) {
      return (float)(phase / HALF_PI);
    }
    else if (phase < 3*HALF_PI) {
      phase -= HALF_PI; // reset starting point to x = 0
      return (float)(-phase/HALF_PI + 1.0);
    }
    else {
      phase -= 3*HALF_PI; // reset starting point to x = 0
      return (float)(phase / HALF_PI - 1.0);
    }
  }
}