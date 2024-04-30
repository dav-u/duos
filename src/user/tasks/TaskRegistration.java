package user.tasks;

import kernel.io.console.Console;

import kernel.scheduler.Scheduler;

public class TaskRegistration {
  public static void registerUserTasks(Scheduler scheduler) {
    TaskManagerTask taskManagerTask = new TaskManagerTask(scheduler, 10);
    taskManagerTask.priority = 3;

    EditorTask editorTask = new EditorTask();
    editorTask.priority = 5;

    PrintPciTask printPciTask = new PrintPciTask();
    printPciTask.priority = 5;

    PrintMemoryMapTask printMemoryMapTask = new PrintMemoryMapTask();
    printMemoryMapTask.priority = 5;

    scheduler.addTask(taskManagerTask);

    taskManagerTask.addTask(editorTask);
    taskManagerTask.addTask(printPciTask);
    taskManagerTask.addTask(printMemoryMapTask);

    Console.print("Use the F-Keys to choose a task");
  }
}