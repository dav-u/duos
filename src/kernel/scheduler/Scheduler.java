package kernel.scheduler;

import kernel.io.keyboard.KeyBufferReader;
import kernel.io.keyboard.Keyboard;
import kernel.io.keyboard.KeyEvent;

import kernel.io.console.*;

public class Scheduler {
  // TODO: maybe a linked list is more elegant than an array?
  private Task[] tasks;
  private int taskCount = 0;
  private KeyBufferReader keyBufferReader;

  public Scheduler() {
    tasks = new Task[10];
    keyBufferReader = new KeyBufferReader(Keyboard.keyBuffer);
  }

  public void run() {
    handleKeyEvents();
  }

  public void handleKeyEvents() {
    KeyEvent keyEvent = keyBufferReader.readNext();

    while (keyEvent != null) {
      for (int i = 0; i < taskCount; i++) {
        tasks[i].handleKeyEvent(keyEvent);
      }

      keyEvent = keyBufferReader.readNext();
    }
  }

  public void addTask(Task task) {
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
}