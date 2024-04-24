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
    // set ebp that is stored on this stack to zero.
    // this way we know when to stop doing a stacktrace.
    int ebp=0;
    MAGIC.inline(0x89, 0x6D); MAGIC.inlineOffset(1, ebp); //mov [ebp+xx],ebp
    MAGIC.wMem32(ebp, 0);

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

    //Console.clear();
    //printSplash();
    //Timer.delay(500);
    //Console.clear();

    //SystemMemoryMap.printSystemMemoryMap();
    //Timer.delay(3000);
    //Console.clear();

    //PCI.printDevices();
    //Timer.delay(3000);
    Console.clear();

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
    splashColor = SymbolColor.setForeground(splashColor, SymbolColor.BLACK);
    splashColor = SymbolColor.setLight(splashColor, false);
    splashColor = SymbolColor.setBackground(splashColor, SymbolColor.TURQUOISE);

    Console.print("\n\n");
    Console.indent = 25;
    Console.print(splashText, splashColor);
    Console.indent = 0;
    Console.print(" David Ulrich Operating System");
  }

  /*
   * Expects to be called in an interrupt.
   */
  @SJC.Inline
  public static void blueScreen(boolean showParameter) {
    byte color = SymbolColor.DEFAULT;
    color = SymbolColor.setBackground(color, SymbolColor.BLUE);
    color = SymbolColor.setLight(color, true);
    //Console.print("BlueScreen", color);

    int ebp=0;
    MAGIC.inline(0x89, 0x6D); MAGIC.inlineOffset(1, ebp); //mov [ebp+xx],ebp

    Console.printHex(ebp, color);

    /*
    PUSHA: (https://c9x.me/x86/html/file_module_x86_id_270.html)
      Temporary = ESP;
      Push(EAX);
      Push(ECX);
      Push(EDX);
      Push(EBX);
      Push(Temporary); # ESP
      Push(EBP);
      Push(ESI);
      Push(EDI);
     */

    int i = 0;
    int prevEbp = MAGIC.rMem32(ebp + i++*4);
    int prevEsp = MAGIC.rMem32(ebp + i++*4);
    int ebx = MAGIC.rMem32(ebp + i++*4);
    int edx = MAGIC.rMem32(ebp + i++*4);
    int ecx = MAGIC.rMem32(ebp + i++*4);
    int eax = MAGIC.rMem32(ebp + i++*4);
    if (showParameter) {
      int parameter = MAGIC.rMem32(ebp + i++*4);
    }

    int prevEip = MAGIC.rMem32(ebp + i++*4);

    Console.cursorIndex = 0;
    Console.print("Registers:\n", color);

    Console.print("EAX=", color);
    Console.printHex(eax, color);
    Console.print('\n');

    Console.print("EBX=", color);
    Console.printHex(ebx, color);
    Console.print('\n');

    Console.print("ECX=", color);
    Console.printHex(ecx, color);
    Console.print('\n');

    Console.print("EDX=", color);
    Console.printHex(edx, color);
    Console.print('\n');

    // https://wiki.osdev.org/Stack_Trace
    Console.print("Stacktrace:\n", color);

    Console.print("EBP=");
    Console.printHex(prevEbp, color);
    Console.print(" EIP=");
    Console.printHex(prevEip, color);
    Console.print('\n');

    while (true) {
      prevEbp = MAGIC.rMem32(prevEbp);
      if (prevEbp == 0) break;

      prevEip = MAGIC.rMem32(prevEbp+4);
      Console.print("EBP=");
      Console.printHex(prevEbp, color);
      Console.print(" EIP=");
      Console.printHex(prevEip, color);
      Console.print('\n');
    }
  }
}
