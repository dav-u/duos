/*
 * A service is a task that cannot handle user input
 */

import kernel.io.keyboard.KeyEvent;
import kernel.scheduler.Task;

public abstract class Service extends Task {
  /*
   * Returns true if it handled the event.
   * Returns false if another low priority task should handle the event (as well).
   */
  public final boolean handleKeyEvent(KeyEvent event) {
    return false;
  }

  /*
   * Returns true if it handled the event.
   * Returns false if another low priority task should handle the event (as well).
   */
  public final boolean handleKeyEventInternal(KeyEvent event) {
    return false;
  }
}