package kernel.io.keyboard;

public abstract class Shortcut {
  public boolean Ctrl;
  public boolean Alt;
  public boolean Shift;

  public int keyCode;

  public abstract void execute();
}