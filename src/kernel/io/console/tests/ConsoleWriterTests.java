package kernel.io.console.tests;

import kernel.io.console.ConsoleWriter;
import kernel.io.console.Console;

/*
 * Provides methods that test the ConsoleWriter
 */
public class ConsoleWriterTests {
  public ConsoleWriter writer;

  public static void testConsoleWriter() {
    ConsoleWriterTests screenWriterTests = new ConsoleWriterTests();
    boolean allTestsPass = true;
    allTestsPass = allTestsPass && screenWriterTests.testWrap();
    allTestsPass = allTestsPass && screenWriterTests.testHex();
    allTestsPass = allTestsPass && screenWriterTests.testInteger();

    Console.clear();
    if (allTestsPass) Console.print("All tests passed");
    else Console.print("Something failed");
  }

  public ConsoleWriterTests() {
    writer = new ConsoleWriter();
  }

  public boolean testWrap() {
    Console.clear();
    for (int i = 0; i < 1900; i++) {
      writer.print('A');
    }

    for (int i = 0; i < 200; i++) {
      writer.print('B');
    }

    return Console.directBuffer.pixels[0].symbol == 'B';
  }

  public boolean testInteger() {
    Console.clear();
    writer.print(1234567);

    return checkScreenTextAt(0, "1234567");
  }

  public boolean testHex() {
    Console.clear();
    writer.printHex((byte)0xEA);
    writer.println();
    writer.printHex((short)0xDEAD);
    writer.println();
    writer.printHex((int)0x1C0FFEE1);
    writer.println();
    writer.printHex((long)0x123456789098765L);

    return 
      checkScreenTextAt(Console.width * 0, "EA")
      && checkScreenTextAt(Console.width * 1, "DEAD")
      && checkScreenTextAt(Console.width * 2, "1C0FFEE1")
      && checkScreenTextAt(Console.width * 3, "0123456789098765");
  }

  private boolean checkScreenTextAt(int cursorIndex, String expectedString) {
    for (int i = 0; i < expectedString.length(); i++) {
      if (Console.directBuffer.pixels[cursorIndex + i].symbol != expectedString.charAt(i))
        return false;
    }

    return true;
  }
}