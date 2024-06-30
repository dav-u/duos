package user.tasks.GraphicSynthesizer;

import kernel.io.GraphicsBuffer;
import math.Math;
import user.lib.audio.Waveform;

public class WaveformDisplay {
  private Waveform waveform;

  public WaveformDisplay(Waveform waveform) {
    this.waveform = waveform;
  }

  public void drawTo(GraphicsBuffer buffer, int x, int y, int width, int height) {
    buffer.drawRectOutline(x, y, width, height, 0xFFFFFFFF);
    buffer.drawHorizontalLine(x, x + width, y + height/2, 0xFF555555);

    int padding = 12; //px
    height -= padding*2;
    y += padding;

    int prevPixelYOffset = 0;

    for (int x_i = 0; x_i < width; x_i++) {
      float phase = (float)((x_i / (float)width) * Math.TAU);
      float amp = -this.waveform.getAmpByPhase(phase);
      int pixelYOffset = (int)((height / 2) * amp); // height = 10px => offset in [-5px, 5px]

      if (prevPixelYOffset < pixelYOffset)
        buffer.drawVerticalLine(x + x_i, y + height / 2 + prevPixelYOffset, y + height / 2 + pixelYOffset, 0xFFCCCCCC);
      else buffer.drawVerticalLine(x + x_i, y + height / 2 + pixelYOffset, y + height / 2 + prevPixelYOffset, 0xFFCCCCCC);

      prevPixelYOffset = pixelYOffset;
    }
  }
}