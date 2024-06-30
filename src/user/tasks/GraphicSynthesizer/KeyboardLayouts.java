package user.tasks.GraphicSynthesizer;

import kernel.io.keyboard.KeyCode;

public class KeyboardLayouts {
  /*
   * Sets the default layout.
   * Keyboard Key A is the Piano Key middle C.
   * Layout of black and white keys is respected.
   * Ends at Keyboard Key Ä.
   */
  public static void setDefaultLayout(int[] keyCodeToKeyIndex) {
    // map keyboard keys to half tone steps
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
    keyCodeToKeyIndex[KeyCode.O] = 13;
    keyCodeToKeyIndex[KeyCode.L] = 14;
    keyCodeToKeyIndex[KeyCode.P] = 15;
    keyCodeToKeyIndex[KeyCode.O_Umlaut] = 16;
    keyCodeToKeyIndex[KeyCode.A_Umlaut] = 17;
  }
}