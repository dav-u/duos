package kernel.io.console;

public class ConsoleSwitchBuffer {
  private short[] bufferMemory;
  private ConsoleBuffer internalBuffer;

  public ConsoleSwitchBuffer() {
    this.bufferMemory = new short[Console.size];
    this.internalBuffer = (ConsoleBuffer)MAGIC.cast2Struct(MAGIC.addr(this.bufferMemory[0]));

    for (int i = 0; i < Console.size; i++) {
      this.internalBuffer.pixels[i].color = SymbolColor.DEFAULT;
      this.internalBuffer.pixels[i].symbol = ' ';
    }
  }

  public int cursorIndex = 0;
  public int indent = 0;

  public ConsoleBufferPixel at(int index) {
    return internalBuffer.pixels[index];
  }
}