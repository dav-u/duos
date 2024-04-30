package kernel.hardware;

import java.lang.Bits;
import kernel.io.console.Console;

public class RTC {
  private static final int RTC_BASE = 0x70;
  private static final int RTC_DATA = 0x71;

  public static int second;
  public static int minute;
  public static int hour;
  public static int day;
  public static int month;
  public static int year;

  /*
   * Hardware interrupts need to be disabled when calling this.
   */
  //// public static void init() {
  ////   // See https://wiki.osdev.org/RTC for RTC
  ////   // and https://wiki.osdev.org/NMI for NMI (disable and enable)

  ////   int prevValue = MAGIC.rIOs8(0x70);
  ////   MAGIC.wIOs8(0x70, (byte)(prevValue | 0x80)); // disable NMI
  ////   // maybe read from 0x71

  ////   prevValue = MAGIC.rIOs8(0x70);
  ////   MAGIC.wIOs8(0x70, (byte)(prevValue & 0x7F)); // disable NMI
  ////   // maybe read from 0x71

  ////   // "It is important to know that upon a IRQ 8, Status Register C will contain a bitmask telling which interrupt happened. [...].
  ////   // What is important is that if register C is not read after an IRQ 8, then the interrupt will not happen again."
  ////   // outportb(0x70, 0x0C);	// select register C
  ////   // inportb(0x71);		// just throw away contents
  //// }

  public static void update() {
    // See https://forum.osdev.org/viewtopic.php?f=1&t=13635

    // wait until no update is in progress
    while (true) {
      MAGIC.wIOs8(RTC_BASE, (byte)0xA); // select register A
      byte value = MAGIC.rIOs8(RTC_DATA);

      if ((value & 0x80) == 0) break;
    }

    MAGIC.wIOs8(RTC_BASE, (byte)0);
    byte secondBcd = MAGIC.rIOs8(RTC_DATA);

    MAGIC.wIOs8(RTC_BASE, (byte)2);
    byte minuteBcd = MAGIC.rIOs8(RTC_DATA);
    
    MAGIC.wIOs8(RTC_BASE, (byte)4);
    byte hourBcd = MAGIC.rIOs8(RTC_DATA);

    MAGIC.wIOs8(RTC_BASE, (byte)7);
    byte dayBcd = MAGIC.rIOs8(RTC_DATA);

    MAGIC.wIOs8(RTC_BASE, (byte)8);
    byte monthBcd = MAGIC.rIOs8(RTC_DATA);

    MAGIC.wIOs8(RTC_BASE, (byte)9);
    byte yearBcd = MAGIC.rIOs8(RTC_DATA);

    second = Bits.twoDigitBcdToInt(secondBcd);
    minute = Bits.twoDigitBcdToInt(minuteBcd);
    hour = Bits.twoDigitBcdToInt(hourBcd);
    day = Bits.twoDigitBcdToInt(dayBcd);
    month = Bits.twoDigitBcdToInt(monthBcd);
    year = Bits.twoDigitBcdToInt(yearBcd);
  }
}