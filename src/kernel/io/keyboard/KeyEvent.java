package kernel.io.keyboard;

public class KeyEvent {
  public final static int Down = 1;
  public final static int Up = 2;
  public final static int Press = 3;

  public Key key;
  public int type;
}