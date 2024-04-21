package kernel.io.keyboard;

import kernel.io.console.Console;

public class KeyboardShortcutInterpreter {
  private int ctrlDown = 0;
  private int altDown = 0;
  private int shiftDown = 0;

  private int shortcutCount;
  private Shortcut[] shortcuts;

  public KeyboardShortcutInterpreter() {
    shortcuts = new Shortcut[10];
  }

  public void addShortcut(Shortcut shortcut) {
    if (shortcutCount == shortcuts.length) {
      enlargeShortcutsArray();
    }

    shortcuts[shortcutCount] = shortcut;

    shortcutCount++;
  }

  /*
   * Returns true if a shortcut was executed by the key event.
   */
  public boolean handleKeyEvent(KeyEvent event) {
    if (KeyCode.removeEscape(event.key.code) == KeyCode.LCtrl) {
      if (event.type == KeyEvent.Down) ctrlDown++;
      else if (event.type == KeyEvent.Up) ctrlDown--;
      return false;
    }

    if (KeyCode.removeEscape(event.key.code) == KeyCode.LShift) {
      if (event.type == KeyEvent.Down) shiftDown++;
      else if (event.type == KeyEvent.Up) shiftDown--;
      return false;
    }

    if (KeyCode.removeEscape(event.key.code) == KeyCode.LAlt) {
      if (event.type == KeyEvent.Down) altDown++;
      else if (event.type == KeyEvent.Up) altDown--;
      return false;
    }

    if (event.type != KeyEvent.Down) return false;

    boolean executedShortcut = false;
    for (int i = 0; i < shortcutCount; i++)
      executedShortcut = executedShortcut || checkAndExecute(shortcuts[i], event);

    return executedShortcut;
  }

  private boolean checkAndExecute(Shortcut shortcut, KeyEvent event) {
    if (shortcut.Shift && shiftDown == 0) return false;
    if (shortcut.Ctrl && ctrlDown == 0) return false;
    if (shortcut.Alt && altDown == 0) return false;

    if (event.key.code == shortcut.keyCode && event.type == KeyEvent.Down) {
      shortcut.execute();
      return true;
    }
  
    return false;
  }

  private void enlargeShortcutsArray() {
    Shortcut[] newShortcuts = new Shortcut[shortcuts.length * 2];
    for (int i = 0; i < shortcuts.length; i++)
      newShortcuts[i] = shortcuts[i];

    shortcuts = newShortcuts;
  }
}