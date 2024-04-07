package kernel;

import kernel.screen.PixelColor;
import kernel.screen.Screen;

public class Timer {
  public final static long millisecondsPerTick = 100; // just a guess

  public static long time = 0;
  public static long delayUntil = 0;

  public static void tick() {
    // should I synchronize access to this method?
    time++;
  }

  /// Waits for the specified amounts of milliseconds before continuing execution.
  public static void delay(int ms) {
    delayUntil = time + ms / millisecondsPerTick;
    Screen.print("Delaying");
    //Screen.print(delayUntil, PixelColor.DEFAULT);

    // just spin until we delayed long enough
    while (time < delayUntil);
    Screen.print("Delayed");
  }
}