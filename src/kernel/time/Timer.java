package kernel.time;

public class Timer {
  public final static long millisecondsPerTick = 55;

  public static long time = 0;
  public static long delayUntil = 0;

  public static void tick() {
    // should I synchronize access to this method?
    time++;
  }

  /*
   * Waits for the specified amounts of milliseconds before continuing execution.
  */
  public static void delay(int ms) {
    delayUntil = time + ms / millisecondsPerTick;

    // just spin until we delayed long enough
    while (time < delayUntil);
  }
}