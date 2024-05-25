package kernel;

import kernel.io.console.*;
import kernel.io.console.tests.*;
import kernel.interrupt.*;
import kernel.io.keyboard.*;
import kernel.io.Graphics;
import kernel.time.Timer;
import kernel.bios.*;
import kernel.hardware.PcSpeaker;
import kernel.hardware.PCI;
import kernel.hardware.RTC;
import kernel.hardware.vesa.VESAGraphics;
import kernel.hardware.vesa.VESAMode;
import kernel.scheduler.*;
import rte.DynamicRuntime;
import rte.SClassDesc;
import rte.SMthdBlock;
import rte.SPackage;
import rte.instancing.GarbageCollectingInstanceCreator;
import user.tasks.TaskRegistration;
import math.Math;

public class Kernel {
  private static String splashText = "\n  _____  _    _  ____   _____  \n |  __ \\| |  | |/ __ \\ / ____| \n | |  | | |  | | |  | | (___   \n | |  | | |  | | |  | |\\___ \\  \n | |__| | |__| | |__| |____) | \n |_____/ \\____/ \\____/|_____/  \n                               \n";

  private static boolean isRunning = true;

  private static Scheduler scheduler;

  public static void main() {
    // set ebp that is stored on this stack to zero.
    // this way we know when to stop doing a stacktrace.
    int ebp=0;
    MAGIC.inline(0x89, 0x6D); MAGIC.inlineOffset(1, ebp); //mov [ebp+xx],ebp
    MAGIC.wMem32(ebp, 0);

    // setup runtime for object allocation
    DynamicRuntime.init();
    Console.clear();
    Console.print("Initialized dynamic runtime\n");

    Interrupts.createInterruptTable();
    Console.print("Created interrupt table\n");

    Keyboard.init();
    Console.print("Initialized keyboard\n");

    // enable hardware interrupts
    Interrupts.initPic();
    Interrupts.setInterruptFlag();
    Console.print("Enabled hardware interrupts\n");

    Memory.initializeVirtualMemory();

    // Timer.delay(1000);

    // Console.clear();
    // printSplash();
    // Timer.delay(1000);
    // Console.clear();

    scheduler = new Scheduler();
    BaseTask baseTask = new BaseTask();
    baseTask.priority = 1;
    scheduler.addTask(baseTask);

    TaskRegistration.registerUserTasks(scheduler);

    // scheduler.printTasks();

    while (isRunning) {
      scheduler.run();
      printTime();
    }

    sendAcpiShutdown();

    // this should not be reached
    Console.print("Failed to shutdown");
    while(true);
  }

  // private static void printPackages(SPackage pkg) {
  //   if (pkg.name != null) Console.println(pkg.name);
  //   else Console.println("<empty>");

  //   SClassDesc currentUnit = pkg.units;

  //   while (currentUnit != null) {
  //     printUnit(currentUnit);
  //     currentUnit = currentUnit.nextUnit;
  //   }

  //   SPackage subPackage = pkg.subPacks;
  //   while (subPackage != null) {
  //     printPackages(subPackage);
  //     subPackage = subPackage.nextPack;
  //   }
  // }

  // private static void printUnit(SClassDesc classDesc) {
  //   Console.println(classDesc.name);
  // }

  public static void checkDynamicObjects(String caller) {
    int count = 1;
    Object current = GarbageCollectingInstanceCreator.firstDynamicObject;
    Object last = GarbageCollectingInstanceCreator.lastObject;

    Console.print("\ncheckDynamicObjects: first=");
    Console.printHex(MAGIC.cast2Ref(current));
    Console.print("; last=");
    Console.printHex(MAGIC.cast2Ref(last));

    Console.print("\n");

    while (current != null) {
      current = current._r_next;
      int addr = MAGIC.cast2Ref(current);
      if (addr < 100) {
        Console.print("\nFound the culprit in ");
        Console.print(caller);
        Console.print(" ");
        Console.printHex(addr);
        Console.print(" at count ");
        Console.print(count);
        Console.print('\n');
        Keyboard.waitFor(KeyCode.Enter);
        break;
      }
      count++;
    }
  }

  public static void panic(int errorCode, String message) {
    Console.cursorIndex = 0;
    Console.print("KERNEL PANIC: ERROR 0x", SymbolColor.RED);
    Console.printHex(errorCode, SymbolColor.RED);
    if (message.length() != 0) {
      Console.print("\nMESSAGE: ", SymbolColor.RED);
      Console.print(message, SymbolColor.RED);
    }
    while(true);
  }

  public static void panic(int errorCode) {
    panic(errorCode, "");
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

    int ebp=0;
    MAGIC.inline(0x89, 0x6D); MAGIC.inlineOffset(1, ebp); //mov [ebp+xx],ebp

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

    Console.print("EBP=", color);
    Console.printHex(prevEbp, color);
    Console.print(" EIP=", color);
    Console.printHex(prevEip, color);
    Console.print("  Method: ", color);
    Console.print(getMethodNameFromIp(prevEip), color);
    Console.print('\n');

    while (true) {
      prevEbp = MAGIC.rMem32(prevEbp);
      if (prevEbp == 0) break;

      prevEip = MAGIC.rMem32(prevEbp+4);
      Console.print("EBP=", color);
      Console.printHex(prevEbp, color);
      Console.print(" EIP=", color);
      Console.printHex(prevEip, color);
      Console.print("  Method: ", color);
      Console.print(getMethodNameFromIp(prevEip), color);
      Console.print('\n');
    }

    while(true);
  }

  public static String getMethodNameFromIp(int instructionPointer) {
    String name = rte.SPackage.root.getMethodNameFromIp(instructionPointer);

    return name != null ? name : "<Unknown>";
  }

  /*
   * This only works inside QEMU emulation.
   * See: https://wiki.osdev.org/Shutdown.
   */
  private static void sendAcpiShutdown() {
    MAGIC.wIOs16(0x604, (short)0x2000);
  }

  private static void printTime() {
    int prevCursorIndex = Console.cursorIndex;
    Console.cursorIndex = Console.size - 17;

    RTC.update();
    if (RTC.day < 10) Console.print('0');
    Console.print(RTC.day);
    Console.print('.');
    if (RTC.month < 10) Console.print('0');
    Console.print(RTC.month);
    Console.print('.');
    Console.print(RTC.year);
    Console.print(' ');

    // just assume GMT+2 for now :D
    if (RTC.hour+2 < 10) Console.print('0');
    Console.print(RTC.hour + 2);
    Console.print(':');
    if (RTC.minute < 10) Console.print('0');
    Console.print(RTC.minute);
    Console.print(':');
    if (RTC.second < 10) Console.print('0');
    Console.print(RTC.second);

    Console.cursorIndex = prevCursorIndex;
  }
}