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

    Screen.clear();

    testScreenWriter();

    while(true);

    // delay(5);
    // Screen.clear();

    ScreenWriter writer = new ScreenWriter();

    TestObject to1 = new TestObject();
    to1.value = 1;
    TestObject to2 = new TestObject();
    to2.value = 2;
    TestObject to3 = new TestObject();
    to3.value = 3;
    TestObject to4 = new TestObject();
    to4.value = 4;

    writer.println(to1.value);
    writer.println(to2.value);
    writer.println(to3.value);
    writer.println(to4.value);

    int to1Addr = MAGIC.cast2Ref(to1);
    int to2Addr = MAGIC.cast2Ref(to2);
    int to3Addr = MAGIC.cast2Ref(to3);
    int to4Addr = MAGIC.cast2Ref(to4);

    writer.printHex(to1Addr);
    Screen.print("\n");
    writer.printHex(to2Addr);
    writer.print(" -> +");
    writer.print(to2Addr - to1Addr);
    Screen.print("\n");
    writer.printHex(to3Addr);
    writer.print(" -> +");
    writer.print(to3Addr - to2Addr);
    Screen.print("\n");
    writer.printHex(to4Addr);
    writer.print(" -> +");
    writer.print(to4Addr - to3Addr);
    Screen.print("\n");

    while (true);
  }

  private static void testScreenWriter() {
    ScreenWriterTests screenWriterTests = new ScreenWriterTests();
    boolean allTestsPass = true;
    allTestsPass = allTestsPass && screenWriterTests.testWrap();
    delay(3);
    allTestsPass = allTestsPass && screenWriterTests.testHex();
    delay(3);
    allTestsPass = allTestsPass && screenWriterTests.testInteger();
    delay(3);

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

  private static void delay(int factor) {
    for (int i = 0; i < factor * 100000000; i++) { }
  }
}
