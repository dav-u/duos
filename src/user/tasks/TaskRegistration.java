package user.tasks;

import kernel.scheduler.Scheduler;

public class TaskRegistration {
  public static void registerUserTasks(Scheduler scheduler) {
    EditorTask editorTask = new EditorTask();
    editorTask.priority = 3;

    scheduler.addTask(editorTask);
    scheduler.setActiveUiTask(editorTask);
  }
}