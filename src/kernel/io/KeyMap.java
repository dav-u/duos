package kernel.io;

/// Maps key codes to Key classes
public class KeyMap {
  private Key[] map;

  /// Creates a new instance of KeyMap using the provided map.
  /// map is not copied. Do not change map after passing it here.
  private KeyMap(Key[] map) {
    this.map = map;
  }

  public Key[] getMap() { return map; }

  public static KeyMap greateGermanKeymap() {
    Key[] map = new Key[Keyboard.keyCount];
    map[KeyboardCode.Esc] = new Key(KeyboardCode.Esc, "Escape", '\0', '\0');
    map[KeyboardCode.N1] = new Key(KeyboardCode.N1, "1", '1', '!');
    map[KeyboardCode.N2] = new Key(KeyboardCode.N2, "2", '2', '"');
    map[KeyboardCode.N3] = new Key(KeyboardCode.N3, "3", '3', '!'); // why does § not work?
    map[KeyboardCode.N3] = new Key(KeyboardCode.N3, "3", '3', '\u00a7');
    map[KeyboardCode.N4] = new Key(KeyboardCode.N4, "4", '4', '$');
    map[KeyboardCode.N5] = new Key(KeyboardCode.N5, "5", '5', '%');
    map[KeyboardCode.N6] = new Key(KeyboardCode.N6, "6", '6', '&');
    map[KeyboardCode.N7] = new Key(KeyboardCode.N7, "7", '7', '/');
    map[KeyboardCode.N8] = new Key(KeyboardCode.N8, "8", '8', '(');
    map[KeyboardCode.N9] = new Key(KeyboardCode.N9, "9", '9', ')');
    map[KeyboardCode.N0] = new Key(KeyboardCode.N0, "0", '0', '=');
    map[KeyboardCode.QuestionMark] = new Key(KeyboardCode.QuestionMark, "ß", 's', '?');

    map[KeyboardCode.Backspace] = new Key(KeyboardCode.Backspace, "Backspace", '\b', '\b');
    map[KeyboardCode.Enter] = new Key(KeyboardCode.Enter, "Enter", '\n', '\n');
    map[KeyboardCode.Tab] = new Key(KeyboardCode.Tab, "Tabulator", '\t', '\t'); // TODO: prints strange character
    map[KeyboardCode.Space] = new Key(KeyboardCode.Space, "Space", ' ', ' ');
    map[KeyboardCode.LShift] = new Key(KeyboardCode.LShift, "LShift", '\0', '\0');
    map[KeyboardCode.RShift] = new Key(KeyboardCode.RShift, "RShift", '\0', '\0');

    map[KeyboardCode.Q] = new Key(KeyboardCode.Q, "Q", 'q', 'Q');
    map[KeyboardCode.W] = new Key(KeyboardCode.W, "W", 'w', 'W');
    map[KeyboardCode.E] = new Key(KeyboardCode.E, "E", 'e', 'E');
    map[KeyboardCode.R] = new Key(KeyboardCode.R, "R", 'r', 'R');
    map[KeyboardCode.T] = new Key(KeyboardCode.T, "T", 't', 'T');
    map[KeyboardCode.Z] = new Key(KeyboardCode.Z, "Z", 'z', 'Z');
    map[KeyboardCode.U] = new Key(KeyboardCode.U, "U", 'u', 'U');
    map[KeyboardCode.I] = new Key(KeyboardCode.I, "I", 'i', 'I');
    map[KeyboardCode.O] = new Key(KeyboardCode.O, "O", 'o', 'O');
    map[KeyboardCode.P] = new Key(KeyboardCode.P, "P", 'p', 'P');
    map[KeyboardCode.U_Umlaut] = new Key(KeyboardCode.U_Umlaut, "U_Umlaut", 'u', 'U');
    map[KeyboardCode.Plus] = new Key(KeyboardCode.Plus, "Plus", '+', '+');

    map[KeyboardCode.A] = new Key(KeyboardCode.A, "A", 'a', 'A');
    map[KeyboardCode.S] = new Key(KeyboardCode.S, "S", 's', 'S');
    map[KeyboardCode.D] = new Key(KeyboardCode.D, "D", 'd', 'D');
    map[KeyboardCode.F] = new Key(KeyboardCode.F, "F", 'f', 'F');
    map[KeyboardCode.G] = new Key(KeyboardCode.G, "G", 'g', 'G');
    map[KeyboardCode.H] = new Key(KeyboardCode.H, "H", 'h', 'H');
    map[KeyboardCode.J] = new Key(KeyboardCode.J, "J", 'j', 'J');
    map[KeyboardCode.K] = new Key(KeyboardCode.K, "K", 'k', 'K');
    map[KeyboardCode.L] = new Key(KeyboardCode.L, "L", 'l', 'L');
    map[KeyboardCode.O_Umlaut] = new Key(KeyboardCode.O_Umlaut, "O_Umlaut", 'o', 'O');
    map[KeyboardCode.A_Umlaut] = new Key(KeyboardCode.A_Umlaut, "A_Umlaut", 'a', 'A');
    map[KeyboardCode.Hash] = new Key(KeyboardCode.Hash, "Hash", '#', '#');

    map[KeyboardCode.Y] = new Key(KeyboardCode.Y, "Y", 'y', 'Y');
    map[KeyboardCode.X] = new Key(KeyboardCode.X, "X", 'x', 'X');
    map[KeyboardCode.C] = new Key(KeyboardCode.C, "C", 'c', 'C');
    map[KeyboardCode.V] = new Key(KeyboardCode.V, "V", 'v', 'V');
    map[KeyboardCode.B] = new Key(KeyboardCode.B, "B", 'b', 'B');
    map[KeyboardCode.N] = new Key(KeyboardCode.N, "N", 'n', 'N');
    map[KeyboardCode.M] = new Key(KeyboardCode.M, "M", 'm', 'M');
    map[KeyboardCode.COMMA] = new Key(KeyboardCode.COMMA, "COMMA", ',', ',');
    map[KeyboardCode.PERIOD] = new Key(KeyboardCode.PERIOD, "PERIOD", '.', ':');
    map[KeyboardCode.MINUS] = new Key(KeyboardCode.MINUS, "MINUS", '-', '_');

    return new KeyMap(map);
  }

  public Key getKeyBy(byte code) {
    return map[code];
  }
}