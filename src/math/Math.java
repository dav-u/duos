package math;

public class Math {
  public static int min(int a, int b) {
    return a <= b ? a : b;
  }

  public static long min(long a, long b) {
    return a <= b ? a : b;
  }

  public static int max(int a, int b) {
    return a >= b ? a : b;
  }

  public static long max(long a, long b) {
    return a >= b ? a : b;
  }

  public static float floor(float value) {
    int intValue = (int) value;
    return (float)(value < intValue ? intValue - 1 : intValue);
  }
}