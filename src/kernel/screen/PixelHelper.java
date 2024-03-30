package kernel.screen;

public class PixelHelper {
  private final static byte foregroundMask = (byte)0x7;  //0b00000111;
  private final static byte backgroundMask = (byte)0x70; //0b01110000;
  private final static byte lightMask = (byte)0x8;       //0b00001000;
  private final static byte blinkMask = (byte)0x80;      //0b10000000;

  public static byte setForeground(byte color, int foreground) {
    return (byte)((color & ~foregroundMask) | foreground);
  }

  public static byte setBackground(byte color, int background) {
    return (byte)((color & ~backgroundMask) | (background << 4));
  }

  public static byte setLight(byte color, boolean value) {
    // TODO: why does the below not work?
    //byte valueAsBit = (byte)(value ? lightMask : 0);
    byte valueAsBit = value ? (byte)lightMask : 0;
    return (byte)((color & ~lightMask) | valueAsBit);
  }

  public static byte setBlink(byte color, boolean value) {
    byte valueAsBit = value ? (byte)blinkMask : 0;
    return (byte)((color & ~blinkMask) | valueAsBit);
  }
}