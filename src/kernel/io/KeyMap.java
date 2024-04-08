package kernel.io;

/// Maps key codes to Key classes
public class KeyMap {
  private static Key[] map;

  public static void init() {
    map = new Key[94];
    map[KeyboardCode.Esc] = new Key(KeyboardCode.Esc, "Escape", '\0', '\0');
    map[KeyboardCode.N1] = new Key(KeyboardCode.N1, "1", '1', '!');
  }

  public static Key getKeyBy(byte code) {
    return map[code];
  }
}