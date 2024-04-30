package java.lang;

public class Bits {
  public static int alignToMultipleOf4Up(int i) {
    return (i + 3) & ~3;
  }

  public static int alignToMultipleOf4Down(int i) {
    return i & ~3;
  }

  public static int twoDigitBcdToInt(int bcd) {
    int tenDigit = ((bcd >>> 4) & 0xF) * 10;
    int oneDigit = bcd & 0xF;

    return tenDigit + oneDigit;
  }
}