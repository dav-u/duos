package kernel.scheduler;

import kernel.BreakpointShortcut;
import kernel.io.console.Console;
import kernel.io.keyboard.KeyEvent;
import kernel.io.keyboard.KeyboardShortcutInterpreter;

public class BaseTask extends Task {
  KeyboardShortcutInterpreter keyboardShortcutInterpreter;
  public BaseTask() {
    keyboardShortcutInterpreter = new KeyboardShortcutInterpreter();
    BreakpointShortcut breakpointShortcut = new BreakpointShortcut();
    keyboardShortcutInterpreter.addShortcut(breakpointShortcut);
  }

  @Override
  public String getName() {
    return "BaseTask";
  }

  @Override
  public boolean handleKeyEvent(KeyEvent event) {
    return keyboardShortcutInterpreter.handleKeyEvent(event);
  }

  @Override
  public boolean run() {
    return false;
  }
}