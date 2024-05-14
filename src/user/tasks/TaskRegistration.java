package user.tasks;

import kernel.io.console.Console;
import kernel.io.console.ConsoleSwitchBuffer;
import kernel.Kernel;
import kernel.Test;
import kernel.scheduler.Scheduler;

public class TaskRegistration {
  public static void registerUserTasks(Scheduler scheduler) {
    TaskManagerTask taskManagerTask = new TaskManagerTask(scheduler, 10);
    taskManagerTask.priority = 3;

    PrintPciTask printPciTask = new PrintPciTask();
    printPciTask.priority = 5;

    EditorTask editorTask = new EditorTask();
    editorTask.priority = 5;

    PrintMemoryMapTask printMemoryMapTask = new PrintMemoryMapTask();
    printMemoryMapTask.priority = 5;

    ShittyPiano shittyPiano = new ShittyPiano();
    shittyPiano.priority = 5;

    WriteToNull writeToNull = new WriteToNull();
    writeToNull.priority = 5;

    scheduler.addTask(taskManagerTask);

    taskManagerTask.addTask(editorTask);
    taskManagerTask.addTask(printPciTask);
    taskManagerTask.addTask(printMemoryMapTask);
    taskManagerTask.addTask(shittyPiano);
    taskManagerTask.addTask(writeToNull);

    Console.print("Use the F-Keys to choose a task");
  }
}