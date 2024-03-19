package kernel;

public class Kernel {
  private static int vidMem = 0xB8000;
  private static int vidOffset = 0;

  public static void main() {
    clearScreen();
    vidOffset = 0;
    // print("Hello World");
    for (int i = 0; i < 256; i++) {
      print("A", i);
    }

    while (true);
  }

  public static void print(String str, int color) {
    int i;
    for (i = 0; i < str.length(); i++)
      print(str.charAt(i), color);
  }

  public static void print(String str) {
    int i;
    for (i = 0; i < str.length(); i++)
      print(str.charAt(i));
  }

  public static void print(char c) {
    print(c, 0x07);
  }

  public static void print(char c, int color) {
    MAGIC.wMem8(vidMem + vidOffset++, (byte) c);
    MAGIC.wMem8(vidMem + vidOffset++, (byte) color);
  }

  public static void clearScreen() {
    vidOffset = 0;
    for (int i = 0; i < 2000; i++)
      print(' ');
  }
}
