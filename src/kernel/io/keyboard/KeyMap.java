package kernel.io.keyboard;

/// Maps key codes to Key classes
public class KeyMap {
  private Key[] map;
  private Key[] e0Map;

  public KeyMap() {
    map = new Key[Keyboard.keyCount];
    e0Map = new Key[Keyboard.keyCount];
  }

  public void set(Key key) {
    if (KeyCode.isE0Code(key.code))
      e0Map[KeyCode.removeEscape(key.code)] = key;
    else map[key.code] = key;
  }

  public static KeyMap greateGermanKeymap() {
    KeyMap map = new KeyMap();

    map.set(new Key(KeyCode.Esc, "Escape", '\0', '\0'));
    map.set(new Key(KeyCode.N1, "1", '1', '!'));
    map.set(new Key(KeyCode.N2, "2", '2', '"'));
    map.set(new Key(KeyCode.N3, "3", '3', '!'));
    map.set(new Key(KeyCode.N3, "3", '3', '\u00a7'));
    map.set(new Key(KeyCode.N4, "4", '4', '$'));
    map.set(new Key(KeyCode.N5, "5", '5', '%'));
    map.set(new Key(KeyCode.N6, "6", '6', '&'));
    map.set(new Key(KeyCode.N7, "7", '7', '/'));
    map.set(new Key(KeyCode.N8, "8", '8', '('));
    map.set(new Key(KeyCode.N9, "9", '9', ')'));
    map.set(new Key(KeyCode.N0, "0", '0', '='));
    map.set(new Key(KeyCode.QuestionMark, "ß", 's', '?'));

    map.set(new Key(KeyCode.Backspace, "Backspace", '\b', '\b'));
    map.set(new Key(KeyCode.Enter, "Enter", '\n', '\n'));
    map.set(new Key(KeyCode.Tab, "Tabulator", '\t', '\t'));
    map.set(new Key(KeyCode.Space, "Space", ' ', ' '));
    map.set(new Key(KeyCode.LShift, "LShift", '\0', '\0'));
    map.set(new Key(KeyCode.RShift, "RShift", '\0', '\0'));
    map.set(new Key(KeyCode.LAlt, "LAlt", '\0', '\0'));
    map.set(new Key(KeyCode.RAlt, "RAlt", '\0', '\0'));
    map.set(new Key(KeyCode.LCtrl, "LCtrl", '\0', '\0'));
    map.set(new Key(KeyCode.RCtrl, "RCtrl", '\0', '\0'));
    
    map.set(new Key(KeyCode.Q, "Q", 'q', 'Q'));
    map.set(new Key(KeyCode.W, "W", 'w', 'W'));
    map.set(new Key(KeyCode.E, "E", 'e', 'E'));
    map.set(new Key(KeyCode.R, "R", 'r', 'R'));
    map.set(new Key(KeyCode.T, "T", 't', 'T'));
    map.set(new Key(KeyCode.Z, "Z", 'z', 'Z'));
    map.set(new Key(KeyCode.U, "U", 'u', 'U'));
    map.set(new Key(KeyCode.I, "I", 'i', 'I'));
    map.set(new Key(KeyCode.O, "O", 'o', 'O'));
    map.set(new Key(KeyCode.P, "P", 'p', 'P'));
    map.set(new Key(KeyCode.U_Umlaut, "U_Umlaut", 'u', 'U'));
    map.set(new Key(KeyCode.Plus, "Plus", '+', '+'));

    map.set(new Key(KeyCode.A, "A", 'a', 'A'));
    map.set(new Key(KeyCode.S, "S", 's', 'S'));
    map.set(new Key(KeyCode.D, "D", 'd', 'D'));
    map.set(new Key(KeyCode.F, "F", 'f', 'F'));
    map.set(new Key(KeyCode.G, "G", 'g', 'G'));
    map.set(new Key(KeyCode.H, "H", 'h', 'H'));
    map.set(new Key(KeyCode.J, "J", 'j', 'J'));
    map.set(new Key(KeyCode.K, "K", 'k', 'K'));
    map.set(new Key(KeyCode.L, "L", 'l', 'L'));
    map.set(new Key(KeyCode.O_Umlaut, "O_Umlaut", 'o', 'O'));
    map.set(new Key(KeyCode.A_Umlaut, "A_Umlaut", 'a', 'A'));
    map.set(new Key(KeyCode.Hash, "Hash", '#', '#'));

    map.set(new Key(KeyCode.Y, "Y", 'y', 'Y'));
    map.set(new Key(KeyCode.X, "X", 'x', 'X'));
    map.set(new Key(KeyCode.C, "C", 'c', 'C'));
    map.set(new Key(KeyCode.V, "V", 'v', 'V'));
    map.set(new Key(KeyCode.B, "B", 'b', 'B'));
    map.set(new Key(KeyCode.N, "N", 'n', 'N'));
    map.set(new Key(KeyCode.M, "M", 'm', 'M'));
    map.set(new Key(KeyCode.COMMA, "COMMA", ',', ','));
    map.set(new Key(KeyCode.PERIOD, "PERIOD", '.', ':'));
    map.set(new Key(KeyCode.MINUS, "MINUS", '-', '_'));

    map.set(new Key(KeyCode.F1, "F1", '\0', '\0'));
    map.set(new Key(KeyCode.F2, "F2", '\0', '\0'));
    map.set(new Key(KeyCode.F3, "F3", '\0', '\0'));
    map.set(new Key(KeyCode.F4, "F4", '\0', '\0'));
    map.set(new Key(KeyCode.F5, "F5", '\0', '\0'));
    map.set(new Key(KeyCode.F6, "F6", '\0', '\0'));
    map.set(new Key(KeyCode.F7, "F7", '\0', '\0'));
    map.set(new Key(KeyCode.F8, "F8", '\0', '\0'));
    map.set(new Key(KeyCode.F9, "F9", '\0', '\0'));
    map.set(new Key(KeyCode.F10, "F10", '\0', '\0'));
    map.set(new Key(KeyCode.F11, "F11", '\0', '\0'));
    map.set(new Key(KeyCode.F12, "F12", '\0', '\0'));

    return map;
  }

  public Key getKeyBy(int code) {
    return KeyCode.isE0Code(code)
      ? e0Map[KeyCode.removeEscape(code)]
      : map[code];
  }
}