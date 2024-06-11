package user.lib.audio;

public abstract class Waveform {
  /*
   * Returns the amplitute for the given phase.
   * Phase goes from 0 to 2 pi.
   */
  public abstract float getAmpByPhase(float phase);
}