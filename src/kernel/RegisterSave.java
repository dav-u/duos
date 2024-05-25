package kernel;

public class RegisterSave {
  public static int esp;
  public static int ebp;

  @SJC.Inline // inline this statement so the caller ebp and esp are used
  public static void saveRegisters() {
    MAGIC.inline(0x89, 0x2D); MAGIC.inlineOffset(4, ebp); //mov [addr(RegisterSave.ebp)],ebp
    MAGIC.inline(0x89, 0x25); MAGIC.inlineOffset(4, esp); //mov [addr(RegisterSave.esp)],esp
  }

  @SJC.Inline // inline this statement so ebp and esp are not overwritten by ret
  public static void restoreRegisters() {
    MAGIC.inline(0x8B, 0x2D); MAGIC.inlineOffset(4, ebp); //mov ebp,[addr(RegisterSave.ebp)]
    MAGIC.inline(0x8B, 0x25); MAGIC.inlineOffset(4, esp); //mov esp,[addr(RegisterSave.esp)]
  }
}