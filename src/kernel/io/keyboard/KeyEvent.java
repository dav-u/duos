package kernel.io.keyboard;

public class KeyEvent {
  /*
   * Down is emitted once when the key is pressed
   * down initially.
   * Down is emitted before Press.
   */
  public final static int Down = 1;

  /*
   * Up is emmitted once the key is released.
   */
  public final static int Up = 2;

  /*
   * Press is emmitted when the key is pressed (like down)
   * and then continues to be emmitted while being held down.
   * Press is emitted after Down.
   */
  public final static int Press = 3;

  public Key key;
  public int type;
}