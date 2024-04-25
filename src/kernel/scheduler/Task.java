package kernel.scheduler;

import kernel.io.keyboard.KeyEvent;

public abstract class Task {
  /*
   * 1 is the highest priority.
   */
  public int priority;

  /*
   * Gets the task name.
   */
  public abstract String getName();

  /*
   * Returns true if it handled the event.
   * Returns false if another low priority task should handle the event (as well).
   */
  public abstract boolean handleKeyEvent(KeyEvent event);

  /*
   * Returns false if it wants to quit.
   */
  public abstract boolean run();
}