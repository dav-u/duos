package kernel.scheduler;

import kernel.io.keyboard.KeyEvent;

/*
 * There is always only one active UI task that can handle input
 * and display something. Other UI tasks might do computations in run
 * even when isActive=false.
 */
public abstract class UiTask extends Task {
  public boolean isActive;
  public void display() { }
  public void onActivate() { }

  /*
   * Returns true if it handled the event.
   * Returns false if another low priority task should handle the event (as well).
   * UiTasks are not allowed to handle key events if they are not active.
   */
  @Override
  public final boolean handleKeyEvent(KeyEvent event) {
    if (isActive)
      return handleKeyEventInternal(event);
    else return false;
  }
}