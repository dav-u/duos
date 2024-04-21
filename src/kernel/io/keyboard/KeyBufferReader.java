package kernel.io.keyboard;

import kernel.interrupt.Interrupts;
import kernel.io.console.Console;

public class KeyBufferReader {
  private KeyBuffer keyBuffer;
  private long nextTimestamp;

  public KeyBufferReader(KeyBuffer keyBuffer) {
    this.keyBuffer = keyBuffer;
    this.nextTimestamp = keyBuffer.getNextTimestamp();
  }

  public KeyEvent readNext() {
    // when I uncomment these pic interrupt lines I cannot type
    // Interrupts.preventPicInterrupts();

    // this can skip key event we were to slow to read
    this.nextTimestamp = keyBuffer.getValidNextTimestamp(nextTimestamp);
    KeyEvent event = keyBuffer.getEvent(nextTimestamp);
    if (event != null)
      nextTimestamp++;

    // Interrupts.restorePicInterrupts();

    return event;
  }
}