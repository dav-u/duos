package kernel;

import kernel.io.console.*;
import kernel.io.console.tests.*;
import kernel.interrupt.*;
import kernel.io.keyboard.*;
import kernel.io.Graphics;
import kernel.time.Timer;
import kernel.bios.*;
import kernel.hardware.PCI;
import kernel.hardware.RTC;
import kernel.hardware.vesa.VESAGraphics;
import kernel.hardware.vesa.VESAMode;
import kernel.scheduler.*;
import rte.DynamicRuntime;
import user.tasks.TaskRegistration;
import math.Math;

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

    // vesa();
    Timer.delay(1000);

    Console.clear();
    printSplash();
    Timer.delay(1000);
    Console.clear();

    // BIOS.switchToGraphicsMode();
    // Graphics.drawRect(0, 0, 20, 20, (byte)0x20);
    // Graphics.render();
    // Timer.delay(1000);
    // BIOS.switchToTextMode();

    // Console.clear();
    // SystemMemoryMap.printSystemMemoryMap();
    // Timer.delay(1000);
    // Console.clear();

    //PCI.printDevices();
    //Timer.delay(3000);

    Scheduler scheduler = new Scheduler();
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

  public static void vesa() {
    VESAGraphics vesaGraphics = VESAGraphics.detectDevice();
    vesaGraphics.setMode(1920, 1080, 32, true);
    int[] col = new int[1920];
    for (int i = 0; i < 1920; i++) {
      col[i] = 0xFFFFFFFF;
    }

    int[][] display = new int[1080][1920];

    // TODO: draw mandelbrot set with display[y][x] = pixel
    // Define the region of the complex plane to visualize
    double xmin = -2.0;
    double xmax = 2.0;
    double ymin = -2.0;
    double ymax = 2.0;

    // Iterate over each pixel in the display array
    for (int y = 0; y < 1080; y++) {
      for (int x = 0; x < 1920; x++) {
        // Map pixel coordinates to the complex plane
        double cx = xmin + (xmax - xmin) * x / (1920 - 1);
        double cy = ymin + (ymax - ymin) * y / (1080 - 1);

        // Mandelbrot set iteration
        double zx = 0;
        double zy = 0;
        int iteration = 0;
        int max_iteration = 50;
        while (zx * zx + zy * zy < 4 && iteration < max_iteration) {
            double temp = zx * zx - zy * zy + cx;
            zy = 2 * zx * zy + cy;
            zx = temp;
            iteration++;
        }
              
        // Color the pixel based on the number of iterations
        if (iteration == max_iteration) {
            display[y][x] = 0x000000; // Black for points in the Mandelbrot set
        } else {
            double hue = (double) iteration / max_iteration;
            display[y][x] = HSBtoRGB((float) hue, 1, 1);
        }
      }
    }

    for (int i = 0; i < 1920; i++)
      vesaGraphics.drawLine(i, display[i]);
    
    //BIOS.switchToTextMode();

    while(true);
  }

  public static int HSBtoRGB(float hue, float saturation, float brightness) {
    int rgb = 0;
    if (saturation == 0) {
      int value = (int) (brightness * 255.0f + 0.5f);
      rgb = (value << 16) | (value << 8) | value;
    } else {
      float h = (hue - (float) Math.floor(hue)) * 6.0f;
      float f = h - (float) Math.floor(h);
      float p = brightness * (1.0f - saturation);
      float q = brightness * (1.0f - saturation * f);
      float t = brightness * (1.0f - (saturation * (1.0f - f)));
      switch ((int) h) {
        case 0:
          rgb = ((int) (brightness * 255.0f + 0.5f) << 16) | ((int) (t * 255.0f + 0.5f) << 8) | (int) (p * 255.0f + 0.5f);
          break;
        case 1:
          rgb = ((int) (q * 255.0f + 0.5f) << 16) | ((int) (brightness * 255.0f + 0.5f) << 8) | (int) (p * 255.0f + 0.5f);
          break;
        case 2:
          rgb = ((int) (p * 255.0f + 0.5f) << 16) | ((int) (brightness * 255.0f + 0.5f) << 8) | (int) (t * 255.0f + 0.5f);
          break;
        case 3:
          rgb = ((int) (p * 255.0f + 0.5f) << 16) | ((int) (q * 255.0f + 0.5f) << 8) | (int) (brightness * 255.0f + 0.5f);
          break;
        case 4:
          rgb = ((int) (t * 255.0f + 0.5f) << 16) | ((int) (p * 255.0f + 0.5f) << 8) | (int) (brightness * 255.0f + 0.5f);
          break;
        case 5:
          rgb = ((int) (brightness * 255.0f + 0.5f) << 16) | ((int) (p * 255.0f + 0.5f) << 8) | (int) (q * 255.0f + 0.5f);
          break;
      }
    }
    return rgb;
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
    Console.print('\n');

    while (true) {
      prevEbp = MAGIC.rMem32(prevEbp);
      if (prevEbp == 0) break;

      prevEip = MAGIC.rMem32(prevEbp+4);
      Console.print("EBP=", color);
      Console.printHex(prevEbp, color);
      Console.print(" EIP=", color);
      Console.printHex(prevEip, color);
      Console.print('\n');
    }
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