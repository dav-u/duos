package kernel;

import kernel.screen.*;

public class Kernel {
  private static String splashText = "\n  _____  _    _  ____   _____  \n |  __ \\| |  | |/ __ \\ / ____| \n | |  | | |  | | |  | | (___   \n | |  | | |  | | |  | |\\___ \\  \n | |__| | |__| | |__| |____) | \n |_____/ \\____/ \\____/|_____/  \n                               \n";

  public static void main() {
    Screen.clearScreen();
    printSplash();

    while (true);
  }

  private static void printSplash() {
    byte splashColor = PixelColor.DEFAULT;
    splashColor = PixelHelper.setForeground(splashColor, PixelColor.BLACK);
    splashColor = PixelHelper.setLight(splashColor, false);
    splashColor = PixelHelper.setBackground(splashColor, PixelColor.TURQUOISE);

    Screen.print("\n\n");
    Screen.indent = 25;
    Screen.print(splashText, splashColor);
    Screen.indent = 0;
    Screen.print(" David Ulrich Operating System");
  }

  private static void printInteger(int integer) {
    // TODO: This is implemented a bit sloppy, but for now it suffices.
    int digitRange = 1000000;

    // make digitRange smaller than (or equal) to integer
    while (digitRange > integer) digitRange /= 10;

    while (digitRange != 0) {
      int digit = integer / digitRange;
      integer -= digitRange * digit;
      char c = (char)(((byte)digit) + ((byte)'0'));

      Screen.print(c);

      digitRange /= 10;
    }
  }
}
