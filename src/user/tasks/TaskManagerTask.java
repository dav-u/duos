package user.tasks;

import kernel.scheduler.Scheduler;
import kernel.scheduler.Task;
import kernel.scheduler.UiTask;
import kernel.io.keyboard.*;
import kernel.io.console.*;

public class TaskManagerTask extends Task {
  private Scheduler scheduler;
  private UiTask[] tasks;
  private int taskCount = 0;

  public TaskManagerTask(Scheduler scheduler, int taskCapacity) {
    tasks = new UiTask[taskCapacity];
    this.scheduler = scheduler;
  }

  /*
   * Gets the task name.
   */
  @Override
  public String getName() {
    return "TaskManager";
  }

  public void addTask(UiTask task) {
    scheduler.addTask(task);
    tasks[taskCount++] = task;
  }

  /*
   * Returns true if it handled the event.
   * Returns false if another low priority task should handle the event (as well).
   */
  @Override
  protected boolean handleKeyEventInternal(KeyEvent event) {
    if (KeyCode.isFKey(event.key.code)) {
      if (event.type != KeyEvent.Down) return true;

      switch (event.key.code) {
        case KeyCode.F1:
          if (tasks[0] != null)
            scheduler.setActiveUiTask(tasks[0]);
          break;
        case KeyCode.F2:
          if (tasks[1] != null)
            scheduler.setActiveUiTask(tasks[1]);
          break;
        case KeyCode.F3:
          if (tasks[2] != null)
            scheduler.setActiveUiTask(tasks[2]);
          break;
        case KeyCode.F4:
          if (tasks[3] != null)
            scheduler.setActiveUiTask(tasks[3]);
          break;
        case KeyCode.F5:
          if (tasks[4] != null)
            scheduler.setActiveUiTask(tasks[4]);
          break;
        case KeyCode.F6:
          if (tasks[5] != null)
            scheduler.setActiveUiTask(tasks[5]);
          break;
        case KeyCode.F7:
          if (tasks[6] != null)
            scheduler.setActiveUiTask(tasks[6]);
          break;
        case KeyCode.F8:
          if (tasks[7] != null)
            scheduler.setActiveUiTask(tasks[7]);
          break;
        case KeyCode.F9:
          if (tasks[8] != null)
            scheduler.setActiveUiTask(tasks[8]);
          break;

        default: return true;
      }
    }

    return false;
  }
}