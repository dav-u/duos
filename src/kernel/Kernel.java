package kernel;

import kernel.screen.*;
import kernel.screen.tests.*;
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

    // when I uncomment this, everything just freezes (like while(true);)
    // Keyboard.init();

    // enable hardware interrupts
    Interrupts.initPic();
    Interrupts.setInterruptFlag();

    testGraphicMode();

    Screen.clear();
    printSplash();
    Timer.delay(2000);
    Screen.clear();

    while (true);
  }

  public static void panic(int errorCode) {
    Screen.cursorIndex = 0;
    Screen.print("KERNEL PANIC: ERROR 0x", PixelColor.RED);
    Screen.printHex(errorCode, PixelColor.RED);
    while(true);
  }

  private static void testGraphicMode() {
    BIOS.regs.EAX=0x0013;
    BIOS.rint(0x10);

    for (int i = 0; i < 320*200; i++)
      MAGIC.wMem8(0xA0000 + i, i%2 == 0 ? (byte)0xAA : (byte)0x33);
    //MAGIC.wMem8(0xA0000, (byte)0x0A);
    
    Timer.delay(2000);

    BIOS.regs.EAX=0x0003;
    BIOS.rint(0x10);
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
