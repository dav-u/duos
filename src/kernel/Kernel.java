package kernel;

import kernel.io.console.*;
import kernel.io.console.tests.*;
import kernel.interrupt.*;
import kernel.io.keyboard.*;
import kernel.io.Graphics;
import kernel.time.Timer;
import kernel.bios.*;
import kernel.hardware.PCI;
import kernel.scheduler.*;
import rte.DynamicRuntime;
import user.tasks.TaskRegistration;

public class Kernel {
  private static String splashText = "\n  _____  _    _  ____   _____  \n |  __ \\| |  | |/ __ \\ / ____| \n | |  | | |  | | |  | | (___   \n | |  | | |  | | |  | |\\___ \\  \n | |__| | |__| | |__| |____) | \n |_____/ \\____/ \\____/|_____/  \n                               \n";

  private static boolean isRunning = true;

  public static void main() {
    // setup runtime for object allocation
    DynamicRuntime.init();

    Interrupts.createInterruptTable();

    Keyboard.init();

    // enable hardware interrupts
    Interrupts.initPic();
    Interrupts.setInterruptFlag();

    // BIOS.switchToGraphicsMode();
    // Graphics.drawRect(0, 0, 20, 20, (byte)0x20);
    // Graphics.render();
    // Timer.delay(1000);
    // BIOS.switchToTextMode();

    Console.clear();
    printSplash();
    Timer.delay(500);
    Console.clear();

    //SystemMemoryMap.printSystemMemoryMap();
    // PCI.printDevices();

    Scheduler scheduler = new Scheduler();
    BaseTask baseTask = new BaseTask();
    baseTask.priority = 1;
    scheduler.addTask(baseTask);

    TaskRegistration.registerUserTasks(scheduler);

    // scheduler.printTasks();

    while (isRunning) {
      scheduler.run();
    }
  }

  public static void panic(int errorCode) {
    Console.cursorIndex = 0;
    Console.print("KERNEL PANIC: ERROR 0x", SymbolColor.RED);
    Console.printHex(errorCode, SymbolColor.RED);
    while(true);
  }

  public static void shutdown() {
    // TODO: let tasks finish up
    isRunning = false;
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
