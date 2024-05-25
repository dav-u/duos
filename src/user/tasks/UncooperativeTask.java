package user.tasks;

import kernel.io.console.Console;
import kernel.scheduler.TextUiTask;

public class UncooperativeTask extends TextUiTask {
  @Override
  public String getName() { return "UncooperativeTask"; }

  @Override
  public void onActivate() {
    super.onActivate();
    Console.clear();
    Console.print("   I am taking soooooo long to process....");
  }

  @Override
  public void display() {
    // display some characters
    while (true) {
      Console.switchBuffer.at(0).symbol++;
      if (Console.switchBuffer.at(0).symbol > 100)
        Console.switchBuffer.at(0).symbol = 48;
      
      // we have to print the current switch buffer ourselves
      // because we never get out of this loop
      Console.printBuffer(Console.switchBuffer);
    }
  }
}