package kernel.io;

public class Key {
  public int code;
  public String name;
  public char character;
  public char shiftCharacter;

  public Key(int code, String name, char character, char shiftCharacter) {
    this.code = code;
    this.name = name;
    this.character = character;
    this.shiftCharacter = shiftCharacter;
  }
}