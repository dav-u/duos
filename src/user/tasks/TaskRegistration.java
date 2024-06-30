package user.tasks;

import kernel.io.console.Console;
import kernel.io.console.ConsoleSwitchBuffer;
import kernel.Kernel;
import kernel.Test;
import kernel.scheduler.Scheduler;
import user.tasks.GraphicSynthesizer.GraphicSynthesizer;

public class TaskRegistration {
  public static void registerUserTasks(Scheduler scheduler) {
    TaskManagerTask taskManagerTask = new TaskManagerTask(scheduler, 20);
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

    UncooperativeTask uncooperativeTask = new UncooperativeTask();
    uncooperativeTask.priority = 5;

    BreakpointTask breakpointTask = new BreakpointTask();
    breakpointTask.priority = 5;

    Synthesizer synthesizer = new Synthesizer();
    synthesizer.priority = 5;

    GraphicSynthesizer graphicSynthesizer = new GraphicSynthesizer();
    graphicSynthesizer.priority = 5;

    scheduler.addTask(taskManagerTask);

    taskManagerTask.addTask(editorTask);
    taskManagerTask.addTask(printPciTask);
    taskManagerTask.addTask(printMemoryMapTask);
    taskManagerTask.addTask(shittyPiano);
    taskManagerTask.addTask(writeToNull);
    taskManagerTask.addTask(uncooperativeTask);
    taskManagerTask.addTask(breakpointTask);
    taskManagerTask.addTask(synthesizer);
    taskManagerTask.addTask(graphicSynthesizer);

    Console.print("Use the F-Keys to choose a task");
  }
}