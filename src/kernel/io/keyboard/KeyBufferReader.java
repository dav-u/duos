package kernel.io.keyboard;

import kernel.io.console.Console;

public class KeyBufferReader {
  private KeyBuffer keyBuffer;
  private long nextTimestamp;

  public KeyBufferReader(KeyBuffer keyBuffer) {
    this.keyBuffer = keyBuffer;
    this.nextTimestamp = keyBuffer.getNextTimestamp();
  }

  public KeyEvent readNext() {
    // this can skip key event we were to slow to read
    this.nextTimestamp = keyBuffer.getValidNextTimestamp(nextTimestamp);
    KeyEvent event = keyBuffer.getEvent(nextTimestamp);
    if (event != null) {
      nextTimestamp++;
      // Console.print("Incremented current timestamp in reader\n");
    }

    // Console.printHex(currentTimestamp, (byte)7);

    return event;
  }
}