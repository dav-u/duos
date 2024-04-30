package kernel.shortcuts;

import kernel.io.keyboard.Shortcut;
import kernel.io.keyboard.KeyCode;
import kernel.io.console.Console;

public class BreakpointShortcut extends Shortcut {
  public BreakpointShortcut() {
    // Ctrl + Alt + C
    this.Ctrl = true;
    this.Alt = true;
    this.keyCode = KeyCode.C;
  }

  public void execute() {
    // int eax = 0;
    // mov    DWORD PTR [ebp+offset],eax
    //MAGIC.inline(0x89, 0x45); // 0x45 is eax
    //MAGIC.inline(0x89, 0x5d); // 0x5d is ebx
    //MAGIC.inlineOffset(1, eax);
    //Console.cursorIndex = 500;
    //Console.printHex(eax, (byte) 7);
    //Console.cursorIndex = 0;
    MAGIC.inline(0xcc);
  }
}