package geometry;

public class Rect {
  public int x, y, w, h;

  public int getTop() {
    return y;
  }

  public int getLeft() {
    return x;
  }

  public int getBottom() {
    return y + h;
  }

  public int getRight() {
    return x + w;
  }
}