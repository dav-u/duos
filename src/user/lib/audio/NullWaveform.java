
package user.lib.audio;

import math.Math;

public class NullWaveform extends Waveform {
  public float getAmpByPhase(float phase) {
    return (float)0.0;
  }
}