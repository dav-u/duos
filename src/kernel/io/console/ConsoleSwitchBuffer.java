package kernel.io.console;

public class ConsoleSwitchBuffer {
  private ConsoleSwitchBufferPixel[] internalBuffer;

  public ConsoleSwitchBuffer() {
    this.internalBuffer = new ConsoleSwitchBufferPixel[Console.size];

    for (int i = 0; i < Console.size; i++) {
      this.internalBuffer[i] = new ConsoleSwitchBufferPixel();
      this.internalBuffer[i].color = SymbolColor.DEFAULT;
      this.internalBuffer[i].symbol = ' ';
    }
  }

  public int cursorIndex = 0;
  public int indent = 0;

  public ConsoleSwitchBufferPixel at(int index) {
    return internalBuffer[index];
  }
}