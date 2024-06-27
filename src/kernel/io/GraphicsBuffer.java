package kernel.io;

import kernel.hardware.vesa.VESAGraphics;

public class GraphicsBuffer {
  public int width, height;
  public int[][] pixelBuffer;

  public GraphicsBuffer(int width, int height) {
    this.width = width;
    this.height = height;

    this.pixelBuffer = new int[height][width];
  }

  public void drawRect(int x, int y, int w, int h, int c) {
    for (int y_i = y; y_i <= y+h; y_i++) {
      for (int x_i = x; x_i <= x+w; x_i++) {
        this.pixelBuffer[y_i][x_i] = c;
      }
    }
  }

  public void renderTo(VESAGraphics graphics) {
    for (int y = 0; y < this.height; y++) {
      graphics.drawLine(y, this.pixelBuffer[y]);
    }
  }
}