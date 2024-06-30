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

  public void setPixel(int x, int y, int c) {
    this.pixelBuffer[y][x] = c;
  }

  public void drawVerticalLine(int x, int yTop, int yBottom, int c) {
    for (int y_i = yTop; y_i <= yBottom; y_i++) {
      this.pixelBuffer[y_i][x] = c;
    }
  }

  public void drawHorizontalLine(int xLeft, int xRight, int y, int c) {
    for (int x_i = xLeft; x_i <= xRight; x_i++) {
      this.pixelBuffer[y][x_i] = c;
    }
  }

  public void drawRectOutline(int x, int y, int w, int h, int c) {
    // top and bottom line
    for (int x_i = x; x_i <= x+w; x_i++) {
      this.pixelBuffer[y][x_i] = c;
      this.pixelBuffer[y+h][x_i] = c;
    }

    // left and right line
    for (int y_i = y; y_i <= y+h; y_i++) {
      this.pixelBuffer[y_i][x] = c;
      this.pixelBuffer[y_i][x+w] = c;
    }
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