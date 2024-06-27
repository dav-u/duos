package user.tasks;

import kernel.io.GraphicsBuffer;
import kernel.io.keyboard.KeyEvent;
import kernel.io.keyboard.KeyCode;
import kernel.io.keyboard.Keyboard;
import kernel.scheduler.GraphicsUiTask;
import math.Math;

public class TestGraphics extends GraphicsUiTask {
  private GraphicsBuffer buffer = new GraphicsBuffer(1920, 1080);

  private int[] keyCodeToKeyIndex = new int[Keyboard.keyCount];
  private boolean[] pressedKeys = new boolean[12];

  public TestGraphics() {
    for (int i = 0; i < Keyboard.keyCount; i++) {
      this.keyCodeToKeyIndex[i] = -1;
    }

    // map keyboard keys to half tone steps
    this.keyCodeToKeyIndex[KeyCode.A] = 0; // A -> 0 (C)
    this.keyCodeToKeyIndex[KeyCode.W] = 1;
    this.keyCodeToKeyIndex[KeyCode.S] = 2;
    this.keyCodeToKeyIndex[KeyCode.E] = 3;
    this.keyCodeToKeyIndex[KeyCode.D] = 4;
    this.keyCodeToKeyIndex[KeyCode.F] = 5;
    this.keyCodeToKeyIndex[KeyCode.T] = 6;
    this.keyCodeToKeyIndex[KeyCode.G] = 7;
    this.keyCodeToKeyIndex[KeyCode.Z] = 8;
    this.keyCodeToKeyIndex[KeyCode.H] = 9;
    this.keyCodeToKeyIndex[KeyCode.U] = 10;
    this.keyCodeToKeyIndex[KeyCode.J] = 11;
    this.keyCodeToKeyIndex[KeyCode.K] = 12;
  }

  @Override
  public String getName() { return "GraphicsTest"; }

  @Override
  public void onActivate() {
    super.onActivate();
  }

  @Override
  public boolean handleKeyEventInternal(KeyEvent event) {
    if (event.type == KeyEvent.Down) {
      int keyIndex = this.keyCodeToKeyIndex[event.key.code];
      if (keyIndex == -1) return true;

      this.pressedKeys[keyIndex] = true;
    }

    if (event.type == KeyEvent.Up) {
      int keyIndex = this.keyCodeToKeyIndex[event.key.code];
      if (keyIndex == -1) return true;

      this.pressedKeys[keyIndex] = false;
    }

    return true;
  }

  @Override
  public void display() {
    int keyHeight = 200;
    int whiteKeyWidth = 50;
    int blackKeyWidth = whiteKeyWidth / 2;
    int keyTop = buffer.height - keyHeight - 500;
    int white = 0xFFFFFFFF;
    int whiteDown = 0xFFAAAAFF;
    int black = 0xFF000000;
    int blackDown = 0xFF0000AA;
    int gap = 2; // px

    for (int k = 0; k < 20; k++) {
      int offset = k*(whiteKeyWidth + gap);
      if (isDisplayKeyPressed(k, true))
        this.buffer.drawRect(offset, keyTop, whiteKeyWidth, keyHeight, whiteDown);
      else this.buffer.drawRect(offset, keyTop, whiteKeyWidth, keyHeight, white);
    }

    for (int k = 0; k < 20; k++) {
      if (k % 7 == 2 || k % 7 == 6) continue;
      int offset = k*(whiteKeyWidth + gap);
      if (isDisplayKeyPressed(k, false))
        this.buffer.drawRect(offset + whiteKeyWidth - blackKeyWidth/2, keyTop, blackKeyWidth, keyHeight/3*2, blackDown);
      else this.buffer.drawRect(offset + whiteKeyWidth - blackKeyWidth/2, keyTop, blackKeyWidth, keyHeight/3*2, black);
    }

    this.buffer.renderTo(this.Graphics);
  }

  private int displayIndexToKeyIndex(int displayIndex, boolean isWhiteKey) {
    if (isWhiteKey) {
      if (displayIndex <= 2) return displayIndex * 2;
      else return displayIndex * 2 - 1;
    }
    else {
      if (displayIndex <= 2) return displayIndex * 2 + 1;
      else return displayIndex * 2;
    }
  }

  private boolean isDisplayKeyPressed(int displayIndex, boolean isWhiteKey) {
    int keyIndex = displayIndexToKeyIndex(displayIndex, isWhiteKey);

    return this.pressedKeys[keyIndex];
  }

  private int keyIndexToDisplayIndex(int keyIndex) {
    int octave = keyIndex / 12;
    keyIndex = keyIndex % 12;
    boolean isWhiteKey = 
        keyIndex <= 3
      ? keyIndex % 2 == 0 // smaller or equal than 3 white keys have even index
      : keyIndex % 2 != 0; // bigger than 3 white keys have odd index
    
    int displayIndex =
        isWhiteKey
      ? (int)Math.ceil(keyIndex / 2.0f)
      : (int)Math.floor(keyIndex / 2.0f);
    
    if (!isWhiteKey) {
      displayIndex |= 0x1000;
    }

    return displayIndex;
  }
}