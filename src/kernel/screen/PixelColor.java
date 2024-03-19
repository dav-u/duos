package kernel.screen;

public class PixelColor extends STRUCT {
  public static final int BLACK = 0;
  public static final int BLUE = 1;
  public static final int GREEN = 2;
  public static final int TURQUOISE = 3;
  public static final int RED = 4;
  public static final int VIOLET = 5;
  public static final int BROWN = 6;
  public static final int GRAY = 7;

  public byte value;

  public void setForeground(int color) {
    // TODO: only set lower three bit
    // value = (byte)color;
    int test = value * 2;
  }
}