package kernel;

import kernel.screen.*;

public class Kernel {
  public static void main() {
    Screen.buffer.pixels[0].symbol = (byte)'B';
    // Screen.buffer.pixels[0].color = 0x07;
    // Screen.buffer.pixels[0].color.setForeground(PixelColor.RED);
    Screen.buffer.pixels[0].color.setForeground(PixelColor.RED);
    Screen.buffer.pixels[0].color.value = 0x4;

    // clearScreen();
    // vidOffset = 0;
    // // print("Hello World");
    // for (int i = 0; i < 256; i++) {
    //   print("A", i);
    // }

    while (true);
  }

  // public static void print(String str, int color) {
  //   int i;
  //   for (i = 0; i < str.length(); i++)
  //     print(str.charAt(i), color);
  // }

  // public static void print(String str) {
  //   int i;
  //   for (i = 0; i < str.length(); i++)
  //     print(str.charAt(i));
  // }

  // public static void print(char c) {
  //   print(c, 0x07);
  // }

  // public static void print(char c, int color) {
  //   MAGIC.wMem8(vidMem + vidOffset++, (byte) c);
  //   MAGIC.wMem8(vidMem + vidOffset++, (byte) color);
  // }

  // public static void clearScreen() {
  //   vidOffset = 0;
  //   for (int i = 0; i < 2000; i++)
  //     print(' ');
  // }
}
