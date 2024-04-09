package kernel.io;

/// Enables drawing in graphics mode.
public class Graphics {
  public static final int width = 320;
  public static final int height = 200;
  public static final int size = width * height;
  private static final int videoMemoryAddress = 0xA0000;

  private static byte[] buffer = new byte[size];

  public static void drawRect(int x, int y, int w, int h, byte color) {
    for (int yi = 0; yi < h; yi++)
      for (int xi = 0; xi < w; xi++)
        drawPixel(xi + x, yi + y, color);
  }

  public static void clear() {
    clear((byte)0x00);
  }

  public static void clear(byte color) {
    drawRect(0, 0, width, height, color);
  }

  public static void drawPixel(int x, int y, byte color) {
    int offset = width * y + x;
    buffer[offset] = color;
  }

  /// Renders the buffer to the screen
  public static void render() {
    for (int i = 0; i < size; i++)
      MAGIC.wMem8(videoMemoryAddress + i, buffer[i]);
  }
}