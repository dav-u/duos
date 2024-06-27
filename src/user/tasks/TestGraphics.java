package user.tasks;

import kernel.io.GraphicsBuffer;
import kernel.io.keyboard.Keyboard;
import kernel.scheduler.GraphicsUiTask;

public class TestGraphics extends GraphicsUiTask {
  private GraphicsBuffer buffer = new GraphicsBuffer(1920, 1080);

  private int[] keyCodeToKeyIndex = new int[Keyboard.keyCount];
  private int[] keyIndexToDisplayKey;

  public TestGraphics() {
    keyCodeToKeyIndex[KeyCode.A] = 0; // A -> 0 (C)
    keyCodeToKeyIndex[KeyCode.W] = 1;
    keyCodeToKeyIndex[KeyCode.S] = 2;
    keyCodeToKeyIndex[KeyCode.E] = 3;
    keyCodeToKeyIndex[KeyCode.D] = 4;
    keyCodeToKeyIndex[KeyCode.F] = 5;
    keyCodeToKeyIndex[KeyCode.T] = 6;
    keyCodeToKeyIndex[KeyCode.G] = 7;
    keyCodeToKeyIndex[KeyCode.Z] = 8;
    keyCodeToKeyIndex[KeyCode.H] = 9;
    keyCodeToKeyIndex[KeyCode.U] = 10;
    keyCodeToKeyIndex[KeyCode.J] = 11;
    keyCodeToKeyIndex[KeyCode.K] = 12;
  }

  @Override
  public String getName() { return "GraphicsTest"; }

  @Override
  public void onActivate() {
    super.onActivate();
  }

  @Override
  public boolean handleKeyEventInternal(KeyEvent event) {
    if (event.type == KeyEvent.Down && event.key.code == KeyCode.Enter) {
      // isPlaying = true;
      this.oscillator.waveform = this.sawtoothWaveform;
    }

    if (event.type == KeyEvent.Up && event.key.code == KeyCode.Enter) {
      // isPlaying = false;
      this.oscillator.waveform = this.nullWaveform;
    }

    return true;
  }

  int[] cols = new int[1920];
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
      if (k == 3)
        this.buffer.drawRect(offset, keyTop, whiteKeyWidth, keyHeight, whiteDown);
      else this.buffer.drawRect(offset, keyTop, whiteKeyWidth, keyHeight, white);
    }

    for (int k = 0; k < 20; k++) {
      if (k % 7 == 2 || k % 7 == 6) continue;
      int offset = k*(whiteKeyWidth + gap);
      if (k == 1)
        this.buffer.drawRect(offset + whiteKeyWidth - blackKeyWidth/2, keyTop, blackKeyWidth, keyHeight/3*2, blackDown);
      else this.buffer.drawRect(offset + whiteKeyWidth - blackKeyWidth/2, keyTop, blackKeyWidth, keyHeight/3*2, black);
    }

    this.buffer.renderTo(this.Graphics);
  }

  private void keyIndexTo(int keyIndex) {
    int octave = keyIndex / 12;
    keyIndex = kexIndex % 12;

  }
}