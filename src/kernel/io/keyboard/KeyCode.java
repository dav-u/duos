package kernel.io.keyboard;

/// These keyboard codes (and the according names) are
/// inspired by the german keyboard layout
/// but can later be remapped in any way using KeyMap.
///
/// TODO: This is not complete yet but a good start.
public class KeyCode {
  public final static int Esc = 0x01;
  public final static int N1 = 0x02;
  public final static int N2 = 0x03;
  public final static int N3 = 0x04;
  public final static int N4 = 0x05;
  public final static int N5 = 0x06;
  public final static int N6 = 0x07;
  public final static int N7 = 0x08;
  public final static int N8 = 0x09;
  public final static int N9 = 0x0A;
  public final static int N0 = 0x0B;
  public final static int QuestionMark = 0x0C; // ß

  public final static int Backspace = 0x0E;
  public final static int Enter = 0x1c;
  public final static int KeypadEnter = 0xe01c;
  public final static int Tab = 15;
  public final static int Space = 57;
  public final static int LShift = 42;
  public final static int RShift = 54;

  // https://www.win.tue.nl/~aeb/linux/kbd/scancodes-1.html#ss1.1
  // e0 1c (Keypad Enter) 		1c (Enter)
  // e0 1d (RCtrl) 		        1d (LCtrl)
  // e0 2a (fake LShift) 		  2a (LShift)
  // e0 35 (Keypad-/) 		    35 (/?)
  // e0 36 (fake RShift) 		  36 (RShift)
  // e0 37 (Ctrl-PrtScn) 		  37 (*/PrtScn)
  // e0 38 (RAlt) 		        38 (LAlt)
  // e0 46 (Ctrl-Break) 		  46 (ScrollLock)
  // e0 47 (Grey Home) 		    47 (Keypad-7/Home)
  // e0 48 (Grey Up) 		      48 (Keypad-8/UpArrow)
  // e0 49 (Grey PgUp) 		    49 (Keypad-9/PgUp)
  // e0 4b (Grey Left) 		    4b (Keypad-4/Left)
  // e0 4d (Grey Right) 		  4d (Keypad-6/Right)
  // e0 4f (Grey End) 		    4f (Keypad-1/End)
  // e0 50 (Grey Down) 		    50 (Keypad-2/DownArrow)
  // e0 51 (Grey PgDn) 		    51 (Keypad-3/PgDn)
  // e0 52 (Grey Insert) 		  52 (Keypad-0/Ins)
  // e0 53 (Grey Delete) 		  53 (Keypad-./Del) 
  public final static int LCtrl = 0x1d;
  public final static int RCtrl = 0xe01d;
  public final static int KeypadDiv = 0xe035;
  public final static int KeypadPlus = 0x4e;
  public final static int KeypadMinus = 0x4a;
  public final static int KeypadTimes = 0x37;
  public final static int LAlt = 0x38;
  public final static int RAlt = 0xe038;
  public final static int Up = 0xe048;
  public final static int Left = 0xe04b;
  public final static int Right = 0xe04d;
  public final static int Down = 0xe050;
  public final static int Insert = 0xe052;

  public final static int Keypad_7_Home = 0x47;
  public final static int Keypad_8_UpArrow = 0x48;
  public final static int Keypad_9_PgUp = 0x49;
  public final static int Keypad_4_Left = 0x4b;
  public final static int Keypad_6_Right = 0x4d;
  public final static int Keypad_1_End = 0x4f;
  public final static int Keypad_2_DownArrow = 0x50;
  public final static int Keypad_3_PgDn = 0x51;
  public final static int Keypad_0_Ins = 0x52;

  public final static int Q = 16;
  public final static int W = 17;
  public final static int E = 18;
  public final static int R = 19;
  public final static int T = 20;
  public final static int Z = 21;
  public final static int U = 22;
  public final static int I = 23;
  public final static int O = 24;
  public final static int P = 25;
  public final static int U_Umlaut = 26;
  public final static int Plus = 27;

  public final static int A = 30;
  public final static int S = 31;
  public final static int D = 32;
  public final static int F = 33;
  public final static int G = 34;
  public final static int H = 35;
  public final static int J = 36;
  public final static int K = 37;
  public final static int L = 38;
  public final static int O_Umlaut = 39;
  public final static int A_Umlaut = 40;
  public final static int Hash = 43;

  public final static int Y = 44;
  public final static int X = 45;
  public final static int C = 46;
  public final static int V = 47;
  public final static int B = 48;
  public final static int N = 49;
  public final static int M = 50;
  public final static int COMMA = 51;
  public final static int PERIOD = 52;
  public final static int MINUS = 53;

  /// Remove the e0 escape scancode from the keycode.
  /// If no e0 scancode is present the original code is returned.
  /// E.g. this makes RAlt to LAlt or NumpadEnter to Enter.
  /// This is generally useful for checking if any Alt or any Ctrl is pressed.
  public static int removeEscape(int code) {
    return code & 0xFF;
  }

  public static boolean isE0Code(int code) {
    return (code & 0xE000) == 0xE000;
  }
}