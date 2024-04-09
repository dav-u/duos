package kernel;

import kernel.io.console.*;
import kernel.io.console.tests.*;
import kernel.interrupt.*;
import kernel.io.Keyboard;
import kernel.io.Key;
import kernel.io.KeyboardCode;
import kernel.time.Timer;
import rte.DynamicRuntime;

public class Kernel {
  private static String splashText = "\n  _____  _    _  ____   _____  \n |  __ \\| |  | |/ __ \\ / ____| \n | |  | | |  | | |  | | (___   \n | |  | | |  | | |  | |\\___ \\  \n | |__| | |__| | |__| |____) | \n |_____/ \\____/ \\____/|_____/  \n                               \n";

  public static void main() {
    // setup runtime for object allocation
    DynamicRuntime.init();

    Interrupts.createInterruptTable();

    Keyboard.init();

    // enable hardware interrupts
    Interrupts.initPic();
    Interrupts.setInterruptFlag();

    // testGraphicMode();

    Console.clear();
    printSplash();
    Timer.delay(500);
    Console.clear();

    while (true);
  }

  public static void panic(int errorCode) {
    Console.cursorIndex = 0;
    Console.print("KERNEL PANIC: ERROR 0x", SymbolColor.RED);
    Console.printHex(errorCode, SymbolColor.RED);
    while(true);
  }

  private static void testGraphicMode() {
    BIOS.switchToGraphicsMode();

    for (int i = 0; i < 320*200; i++)
      MAGIC.wMem8(0xA0000 + i, i%2 == 0 ? (byte)0xAA : (byte)0x33);
    //MAGIC.wMem8(0xA0000, (byte)0x0A);
    
    Timer.delay(2000);

    BIOS.switchToTextMode();
  }

  private static void printSplash() {
    byte splashColor = SymbolColor.DEFAULT;
    splashColor = PixelHelper.setForeground(splashColor, SymbolColor.BLACK);
    splashColor = PixelHelper.setLight(splashColor, false);
    splashColor = PixelHelper.setBackground(splashColor, SymbolColor.TURQUOISE);

    Console.print("\n\n");
    Console.indent = 25;
    Console.print(splashText, splashColor);
    Console.indent = 0;
    Console.print(" David Ulrich Operating System");
  }
}
