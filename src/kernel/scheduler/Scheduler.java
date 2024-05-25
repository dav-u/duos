package kernel.scheduler;

import kernel.io.keyboard.KeyBufferReader;
import kernel.io.keyboard.Keyboard;
import kernel.io.keyboard.KeyEvent;
import kernel.io.keyboard.KeyCode;
import kernel.ErrorCode;
import kernel.Kernel;
import kernel.io.console.*;

public class Scheduler {
  // TODO: maybe a linked list is more elegant than an array?
  private Task[] tasks;
  private UiTask activeUiTask;
  private int taskCount = 0;
  private KeyBufferReader keyBufferReader;

  private int currentTaskIndex = -1;

  public Scheduler() {
    tasks = new Task[10];
    keyBufferReader = new KeyBufferReader(Keyboard.keyBuffer);
    activeUiTask = null;
  }

  public void printTasks() {
    for (int i = 0; i < taskCount; i++) {
      Console.print(tasks[i].getName());
      Console.print('\n');
    }
  }

  public void displayUi() {
    if (activeUiTask == null) return;

    activeUiTask.display();
  }

  public void setActiveUiTask(UiTask task) {
    if (activeUiTask != null) {
      activeUiTask.isActive = false;
    }

    activeUiTask = task;
    activeUiTask.isActive = true;

    activeUiTask.onActivate();
  }

  public void run() {
    handleKeyEvents();
    // runTasks(); // TODO: let tasks run
    displayUi();
  }

  public void runTasks() {
    for (int i = 0; i < taskCount; i++) {
      tasks[i].run(); // TODO: handle return value
    }
  }

  public void handleKeyEvents() {
    KeyEvent keyEvent = keyBufferReader.readNext();

    while (keyEvent != null) {
      for (int i = 0; i < taskCount; i++) {
        boolean wasHandled = tasks[i].handleKeyEvent(keyEvent);
        if (wasHandled) break;
      }

      keyEvent = keyBufferReader.readNext();
    }
  }

  public void removeTask(Task task) {
    if (task == activeUiTask) activeUiTask = null;

    for (int i = 0; i < taskCount; i++) {
      if (task == tasks[i]) {
        removeTaskAt(i);
        return;
      }
    }
  }

  public void addTask(Task task) {
    if (!(task instanceof BaseTask) && task.priority <= 1)
      Kernel.panic(ErrorCode.InvalidTaskPriority);

    for (int i = 0; i < taskCount; i++) {
      Task presentTask = tasks[i];

      // when the task to add has a higher priority than one
      // of the present task we put the new task into the place
      // of the present task and reinsert the low priority task
      if (presentTask.priority > task.priority) {
        tasks[i] = task;
        addTask(presentTask);
        return;
      }
    }

    // if we task to insert has lower priority than every other 
    // present task, just append it
    tasks[taskCount++] = task;
  }

  public UiTask getActiveUiTask() {
    return activeUiTask;
  }

  private void removeTaskAt(int index) {
    for (; index < taskCount - 1; index++) {
      tasks[index] = tasks[index + 1];
    }

    taskCount--;
  }
}