package kernel.interrupt;

import kernel.ErrorCode;
import kernel.Kernel;
import kernel.Timer;
import kernel.screen.*;

public class Handlers {
  @SJC.Interrupt
  public static void DivideError() {
    Kernel.panic(ErrorCode.DivisionByZero);
  }

  @SJC.Interrupt
  public static void Breakpoint() {
    Screen.print("Breakpoint");
  }

  @SJC.Interrupt
  public static void Timer() {
    Timer.tick();
    Interrupts.acknowledgePicMasterInterrupt();
  }
}