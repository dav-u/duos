package kernel;

import kernel.screen.*;

public class Kernel {
  private static String splashText = "\n  _____  _    _  ____   _____  \n |  __ \\| |  | |/ __ \\ / ____| \n | |  | | |  | | |  | | (___   \n | |  | | |  | | |  | |\\___ \\  \n | |__| | |__| | |__| |____) | \n |_____/ \\____/ \\____/|_____/  \n                               \n";

  public static void main() {
    Screen.clear();
    printSplash();

    Screen.clear();

    testScreenWriter();

    while(true);

    return;

    TestObject to1 = new TestObject();
    to1.value = 1;
    TestObject to2 = new TestObject();
    to2.value = 2;
    TestObject to3 = new TestObject();
    to3.value = 3;
    TestObject to4 = new TestObject();
    to4.value = 4;

    Screen.printInteger(to1.value);
    Screen.print("\n");
    Screen.printInteger(to2.value);
    Screen.print("\n");
    Screen.printInteger(to3.value);
    Screen.print("\n");
    Screen.printInteger(to4.value);
    Screen.print("\n");

    int to1Addr = MAGIC.cast2Ref(to1);
    int to2Addr = MAGIC.cast2Ref(to2);
    int to3Addr = MAGIC.cast2Ref(to3);
    int to4Addr = MAGIC.cast2Ref(to4);

    Screen.printInteger(to1Addr);
    Screen.print("\n");
    Screen.printInteger(to2Addr);
    Screen.print("\n");
    Screen.printInteger(to3Addr);
    Screen.print("\n");
    Screen.printInteger(to4Addr);
    Screen.print("\n");

    while (true);
  }

  private static void testScreenWriter() {
    ScreenWriterTests screenWriterTests = new ScreenWriterTests();
    boolean allTestsPass = true;
    allTestsPass = allTestsPass && screenWriterTests.testWrap();
    allTestsPass = allTestsPass && screenWriterTests.testColor();

    // Screen.clear();
    // if (allTestsPass) Screen.print("All tests passed");
    // else Screen.print("Something failed");
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
}
