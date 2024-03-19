package kernel.screen;

public class Screen {
  // Screen
  private static final int screenMemoryAddress = 0xB8000;
  public static ScreenBuffer buffer = (ScreenBuffer)MAGIC.cast2Struct(screenMemoryAddress);

  public static final int size = 2000;
  public static final int width = 80;
  public static final int height = 25;

  public static int cursorIndex = 0;

  public static void print(String str) {
    for (int i = 0; i < str.length(); i++) {
      print(str.charAt(i));
    }
  }

  public static void print(char c) {
    buffer.pixels[cursorIndex++].symbol = (byte)c;
  }

  public static void print(char c, PixelColor color) {
    buffer.pixels[cursorIndex++].symbol = (byte)c;
  }
}