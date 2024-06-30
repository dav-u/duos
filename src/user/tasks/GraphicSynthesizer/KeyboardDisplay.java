package user.tasks.GraphicSynthesizer;

import kernel.io.GraphicsBuffer;

public class KeyboardDisplay {
  public int keyHeight = 200;
  public int whiteKeyWidth = 50;
  public int blackKeyWidth = whiteKeyWidth / 2;
  public int keyTop;
  public int white = 0xFFFFFFFF;
  public int whiteDown = 0xFFAAAAFF;
  public int black = 0xFF000000;
  public int blackDown = 0xFF0000AA;
  public int gap = 2; // px

  public KeyboardDisplay(int keyTop) {
    this.keyTop = keyTop;
  }

  public void drawTo(GraphicsBuffer buffer, boolean[] pressedKeys) {
    for (int k = 0; k < 11; k++) {
      int offset = k*(whiteKeyWidth + gap);
      if (isDisplayKeyPressed(pressedKeys, k, true))
        buffer.drawRect(offset, keyTop, whiteKeyWidth, keyHeight, whiteDown);
      else buffer.drawRect(offset, keyTop, whiteKeyWidth, keyHeight, white);
    }

    for (int k = 0; k < 10; k++) {
      if (k % 7 == 2 || k % 7 == 6) continue;
      int offset = k*(whiteKeyWidth + gap);
      if (isDisplayKeyPressed(pressedKeys, k, false))
        buffer.drawRect(offset + whiteKeyWidth - blackKeyWidth/2, keyTop, blackKeyWidth, keyHeight/3*2, blackDown);
      else buffer.drawRect(offset + whiteKeyWidth - blackKeyWidth/2, keyTop, blackKeyWidth, keyHeight/3*2, black);
    }
  }

  private int displayIndexToKeyIndex(int displayIndex, boolean isWhiteKey) {
    int octave = displayIndex / 7;
    displayIndex %= 7;

    if (isWhiteKey) {
      if (displayIndex <= 2) return 12 * octave + displayIndex * 2;
      else return 12 * octave + displayIndex * 2 - 1;
    }
    else {
      if (displayIndex <= 2) return 12 * octave + displayIndex * 2 + 1;
      else return 12 * octave + displayIndex * 2;
    }
  }

  private boolean isDisplayKeyPressed(boolean[] pressedKeys, int displayIndex, boolean isWhiteKey) {
    int keyIndex = displayIndexToKeyIndex(displayIndex, isWhiteKey);

    return pressedKeys[keyIndex];
  }
}