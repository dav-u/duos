package java.lang;

public class Bits {
  public static int alignToMultipleOf4Up(int i) {
    return (i + 3) & ~3;
  }

  public static int alignToMultipleOf4Down(int i) {
    return i & ~3;
  }
}