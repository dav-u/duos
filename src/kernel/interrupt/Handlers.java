package kernel.interrupt;

import kernel.ErrorCode;
import kernel.Kernel;
import kernel.io.keyboard.Keyboard;
import kernel.time.Timer;
import kernel.io.console.*;

/*
 * Handlers for various interrupts.
 * Extra info in comments is from https://wiki.osdev.org/Exceptions.
 */
public class Handlers {

  /*
   * The Division Error occurs when dividing any number by 0 using the DIV or IDIV instruction, or when the division result is too large to be represented in the destination. Since a faulting DIV or IDIV instruction is very easy to insert anywhere in the code, many OS developers use this exception to test whether their exception handling code works.
   * The saved instruction pointer points to the DIV or IDIV instruction which caused the exception. 
   */
  @SJC.Interrupt
  public static void DivideError() {
    Kernel.panic(ErrorCode.DivisionByZero);
  }

  /*
   * The Debug exception occurs on the following conditions:
   *     Instruction fetch breakpoint (Fault)
   *     General detect condition (Fault)
   *     Data read or write breakpoint (Trap)
   *     I/O read or write breakpoint (Trap)
   *     Single-step (Trap)
   *     Task-switch (Trap) 
   * When the exception is a fault, the saved instruction pointer points to the instruction which caused the exception. When the exception is a trap, the saved instruction pointer points to the instruction after the instruction which caused the exception.
   * Error code: The Debug exception does not set an error code. However, exception information is provided in the debug registers (CPU_Registers_x86#Debug_Registers). 
  */
  @SJC.Interrupt
  public static void DebugException() {
    Console.print("DebugException");
  }

  /*
   * NMIs occur for RAM errors and unrecoverable hardware problems.
  */
  @SJC.Interrupt
  public static void NonMaskableInterrupt() {
    Kernel.panic(ErrorCode.Nmi);
  }

  /*
   * A Breakpoint exception occurs at the execution of the INT3 instruction. Some debug software replace an instruction by the INT3 instruction. When the breakpoint is trapped, it replaces the INT3 instruction with the original instruction, and decrements the instruction pointer by one.
   * The saved instruction pointer points to the byte after the INT3 instruction. 
  */
  @SJC.Interrupt
  public static void Breakpoint() {
    Kernel.blueScreen(false);
    // Console.print("Breakpoint");
    while(true);
  }

  /*
   * Overflow exception
   * An Overflow exception is raised when the INTO instruction is executed while the overflow bit in RFLAGS is set to 1.
   * The saved instruction pointer points to the instruction after the INTO instruction. 
  */
  @SJC.Interrupt
  public static void Into() {
    Console.print("INTO (Overflow)");
  }

  /*
   * This exception can occur when the BOUND instruction is executed. The BOUND instruction compares an array index with the lower and upper bounds of an array. When the index is out of bounds, the Bound Range Exceeded exception occurs.
   * The saved instruction pointer points to the BOUND instruction which caused the exception. 
  */
  @SJC.Interrupt
  public static void IndexOutOfRange() {
    Console.print("Index out of range");
  }

  /*
   * The Invalid Opcode exception occurs when the processor tries to execute an invalid or undefined opcode, or an instruction with invalid prefixes. It also occurs in other cases, such as:
   *     The instruction length exceeds 15 bytes, but this only occurs with redundant prefixes.
   *     The instruction tries to access a non-existent control register (for example, mov cr6, eax).
   *     The UD instruction is executed. 
   * The saved instruction pointer points to the instruction which caused the exception. 
  */
  @SJC.Interrupt
  public static void InvalidOpcode() {
    Kernel.panic(ErrorCode.InvalidOpcode);
  }

  /*
   * A Double Fault occurs when an exception is unhandled or when an exception occurs while the CPU is trying to call an exception handler. Normally, two exception at the same time are handled one after another, but in some cases that is not possible. For example, if a page fault occurs, but the exception handler is located in a not-present page, two page faults would occur and neither can be handled. A double fault would occur.
   * A double fault will always generate an error code with a value of zero.
   * The saved instruction pointer is undefined. A double fault cannot be recovered. The faulting process must be terminated. 
  */
  @SJC.Interrupt
  public static void DoubleFault() {
    Kernel.panic(ErrorCode.InvalidOpcode);
  }

  /*
   * A General Protection Fault may occur for various reasons. The most common are:
   *     Segment error (privilege, type, limit, read/write rights).
   *     Executing a privileged instruction while CPL != 0.
   *     Writing a 1 in a reserved register field or writing invalid value combinations (e.g. CR0 with PE=0 and PG=1).
   *     Referencing or accessing a null-descriptor.
   *     Accessing a memory address with bits 48-63 not matching bit 47 (e.g. 0x_0000_8000_0000_0000 instead of 0x_ffff_8000_0000_0000) in 64 bit mode. 
   * The saved instruction pointer points to the instruction which caused the exception.
   * Error code: The General Protection Fault sets an error code, which is the segment selector index when the exception is segment related. Otherwise, 0. 
   */
  @SJC.Interrupt
  public static void GeneralProtectionError() {
    Kernel.panic(ErrorCode.InvalidOpcode);
  }

  /*
   * A Page Fault occurs when:
   *     A page directory or table entry is not present in physical memory.
   *     Attempting to load the instruction TLB with a translation for a non-executable page.
   *     A protection check (privileges, read/write) failed.
   *     A reserved bit in the page directory or table entries is set to 1. 
   * The saved instruction pointer points to the instruction which caused the exception. 
  */
  @SJC.Interrupt
  public static void PageFault() {
    Kernel.blueScreen(true);
  }

  @SJC.Interrupt
  public static void Timer() {
    Timer.tick();
    Interrupts.acknowledgePicMasterInterrupt();
  }

  @SJC.Interrupt
  public static void Keyboard() {
    Keyboard.readIoBuffer();
    Interrupts.acknowledgePicMasterInterrupt();
  }
}