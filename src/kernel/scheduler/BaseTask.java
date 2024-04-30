package kernel.scheduler;

import kernel.shortcuts.BreakpointShortcut;
import kernel.shortcuts.ShutdownShortcut;
import kernel.io.console.Console;
import kernel.io.keyboard.KeyEvent;
import kernel.io.keyboard.KeyboardShortcutInterpreter;

public class BaseTask extends Task {
  private KeyboardShortcutInterpreter keyboardShortcutInterpreter;

  public BaseTask() {
    keyboardShortcutInterpreter = new KeyboardShortcutInterpreter();

    keyboardShortcutInterpreter.addShortcut(new BreakpointShortcut());
    keyboardShortcutInterpreter.addShortcut(new ShutdownShortcut());
  }

  @Override
  public String getName() {
    return "BaseTask";
  }

  @Override
  public boolean handleKeyEventInternal(KeyEvent event) {
    return keyboardShortcutInterpreter.handleKeyEvent(event);
  }

  @Override
  public boolean run() {
    return false;
  }
}