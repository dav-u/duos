package kernel.io.keyboard;

import kernel.io.console.Console;

public class KeyboardShortcutInterpreter {
  private KeyBufferReader keyBufferReader;

  private int ctrlDown = 0;
  private int altDown = 0;
  private int shiftDown = 0;

  private int shortcutCount;
  private Shortcut[] shortcuts;

  public KeyboardShortcutInterpreter(KeyBufferReader keyBufferReader) {
    this.keyBufferReader = keyBufferReader;
    shortcuts = new Shortcut[10];
  }

  public void addShortcut(Shortcut shortcut) {
    if (shortcutCount == shortcuts.length) {
      enlargeShortcutsArray();
    }

    shortcuts[shortcutCount] = shortcut;

    shortcutCount++;
  }

  public void execute() {
    KeyEvent event = keyBufferReader.readNext();
    if (event == null) return;

    // Console.print(event.key.name);
    // Console.print('\n');

    if (KeyCode.removeEscape(event.key.code) == KeyCode.LCtrl) {
      if (event.type == KeyEvent.Down) ctrlDown++;
      else if (event.type == KeyEvent.Up) ctrlDown--;
      return;
    }

    if (KeyCode.removeEscape(event.key.code) == KeyCode.LShift) {
      if (event.type == KeyEvent.Down) shiftDown++;
      else if (event.type == KeyEvent.Up) shiftDown--;
      return;
    }

    if (KeyCode.removeEscape(event.key.code) == KeyCode.LAlt) {
      if (event.type == KeyEvent.Down) altDown++;
      else if (event.type == KeyEvent.Up) altDown--;
      return;
    }

    if (event.type != KeyEvent.Down) return;

    for (int i = 0; i < shortcutCount; i++)
      checkAndExecute(shortcuts[i], event);
  }

  private void checkAndExecute(Shortcut shortcut, KeyEvent event) {
    if (shortcut.Shift && shiftDown == 0) return;
    if (shortcut.Ctrl && ctrlDown == 0) return;
    if (shortcut.Alt && altDown == 0) return;

    if (event.key.code == shortcut.keyCode && event.type == KeyEvent.Down)
      shortcut.execute();
  }

  private void enlargeShortcutsArray() {
    Shortcut[] newShortcuts = new Shortcut[shortcuts.length * 2];
    for (int i = 0; i < shortcuts.length; i++)
      newShortcuts[i] = shortcuts[i];

    shortcuts = newShortcuts;
  }
}