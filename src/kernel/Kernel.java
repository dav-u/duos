package kernel;

import kernel.screen.*;
import kernel.screen.tests.*;
import rte.DynamicRuntime;

public class Kernel {
  private static String splashText = "\n  _____  _    _  ____   _____  \n |  __ \\| |  | |/ __ \\ / ____| \n | |  | | |  | | |  | | (___   \n | |  | | |  | | |  | |\\___ \\  \n | |__| | |__| | |__| |____) | \n |_____/ \\____/ \\____/|_____/  \n                               \n";

  public static void main() {
    DynamicRuntime.init();

    Screen.clear();
    printSplash();
    delay(2);
    Screen.clear();

    while (true);
  }

  private static void testScreenWriter() {
    ScreenWriterTests screenWriterTests = new ScreenWriterTests();
    boolean allTestsPass = true;
    allTestsPass = allTestsPass && screenWriterTests.testWrap();
    allTestsPass = allTestsPass && screenWriterTests.testHex();
    allTestsPass = allTestsPass && screenWriterTests.testInteger();

    Screen.clear();
    if (allTestsPass) Screen.print("All tests passed");
    else Screen.print("Something failed");
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

  // a hacky delay function. I do not have interrupts yet :(
  private static void delay(int factor) {
    for (int i = 0; i < factor * 100000000; i++) { }
  }
}
