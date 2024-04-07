package kernel;

import kernel.screen.*;
import kernel.screen.tests.*;
import kernel.interrupt.*;
import kernel.time.Timer;
import rte.DynamicRuntime;

public class Kernel {
  private static String splashText = "\n  _____  _    _  ____   _____  \n |  __ \\| |  | |/ __ \\ / ____| \n | |  | | |  | | |  | | (___   \n | |  | | |  | | |  | |\\___ \\  \n | |__| | |__| | |__| |____) | \n |_____/ \\____/ \\____/|_____/  \n                               \n";

  public static void main() {
    // setup runtime for object allocation
    DynamicRuntime.init();

    // setup interrupts
    Interrupts.createInterruptTable();
    Interrupts.initPic();
    Interrupts.setInterruptFlag();

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

    //for (int i = 0; i < 320*200; i++)
    //  MAGIC.wMem8(0xA0000 + i, i%2 == 0 ? (byte)0xAA : (byte)0x33);
    MAGIC.wMem8(0xA0000, (byte)0x0A);
    
    delay(5);

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

  // a hacky delay function. I do not have interrupts yet :(
  private static void delay(int factor) {
    for (int i = 0; i < factor * 100000000; i++) { }
  }
}
