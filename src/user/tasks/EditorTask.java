package user.tasks;

import kernel.io.keyboard.KeyboardTextInterpreter;
import kernel.io.keyboard.KeyEvent;
import kernel.scheduler.UiTask;
import kernel.io.console.Console;

public class EditorTask extends UiTask {
  KeyboardTextInterpreter keyboardTextInterpreter;

  public EditorTask() {
    keyboardTextInterpreter = new KeyboardTextInterpreter();
  }

  @Override
  public String getName() {
    return "EditorTask";
  }

  @Override
  public void display() { }

  @Override
  public void onActivate() {
    Console.clear();
    Console.print("Editor:\n");
  }
  
  /*
   * Returns true if it handled the event.
   * Returns false if another low priority task should handle the event (as well).
   */
  @Override
  public boolean handleKeyEventInternal(KeyEvent event) {
    // KeyboardTextInterpreter keyboardTextInterpreter = new KeyboardTextInterpreter(keyBufferReader);
    keyboardTextInterpreter.handleKeyEvent(event);
    char c = keyboardTextInterpreter.getChar();
    if (c != '\0')
      Console.print(c);

    return true;
  }

  /*
   * Returns false if it wants to quit.
   */
  @Override
  public boolean run() {
    return true;
  }
}