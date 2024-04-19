package kernel.io.keyboard;

import kernel.io.console.SymbolColor;
import kernel.io.console.Console;
import math.Math;

/*
 * - One Buffer with everything that happened regarding the keyboard
 * - Ringbuffer for fixed memory footprint
 * - Error handling when reader does not read for too long (ring buffer overrides old data)
 * - Enable multiple readers (multiple indices)
 * - Nice interface for KeyPress, KeyUp, KeyDown
 */

public class KeyBuffer {
  /// size of the buffer (set on initialization)
  public int size;

  /// timestamp of the latest value that is present in the buffer
  private long currentTimestamp = -1;

  /// the next index used inside the ring buffer (never exceeds size)
  private int nextIndex = 0;

  /// the underlying buffer that is indexed as a ring buffer
  private KeyEvent[] keyEvents;

  public KeyBuffer(int size) {
    this.size = size;
    keyEvents = new KeyEvent[size];

    for (int i = 0; i < size; i++) {
      keyEvents[i] = new KeyEvent();
    }
  }

  public void appendEvent(Key key, int type) {
    keyEvents[nextIndex].key = key;
    keyEvents[nextIndex].type = type;

    incrementIndex();
  }

  /// Returns the event with the specified timestamp.
  /// DO NOT MODIFY the returned event.
  /// (I would like to return by value (like struct in c++) but I do not know if this is possible with SJC)
  public KeyEvent getEvent(long timestampToGet) {
    // gefährlich
    if (timestampToGet > currentTimestamp) return null; // no more events

    int offset = (int)(currentTimestamp - timestampToGet);
    if (offset > size) return null; // I should return something different (too slow reading events)

    int eventIndex = (nextIndex - 1) - offset;
    if (eventIndex < 0) eventIndex += size;

    return keyEvents[eventIndex];
  }

  public long getNextTimestamp() {
    return currentTimestamp + 1;
  }

  public long getValidNextTimestamp(long requestedNextTimestamp) {
    // cT = 10; s = 3; valid t = {8, 9, 10}
    // 8 = ct - s + 1
    return Math.max(requestedNextTimestamp, currentTimestamp - size + 1);
  }

  private void incrementIndex() {
    nextIndex++;
    currentTimestamp++;
    nextIndex %= size;
  }
}