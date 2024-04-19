package kernel.bios;

import kernel.io.console.Console;
import kernel.Kernel;
import kernel.ErrorCode;

public class SystemMemoryMap {
  /*
  * Values for System Memory Map address type:
  * 01h    memory, available to OS
  * 02h    reserved, not available (e.g. system ROM, memory-mapped device)
  * 03h    ACPI Reclaim Memory (usable by OS after reading ACPI tables)
  * 04h    ACPI NVS Memory (OS is required to save this memory between NVS sessions)
  * other  not defined yet -- treat as Reserved
  */
  public static class SystemMemoryMapAddressType {
    public final static int AvailableToOs = 0x1;
    public final static int Reserved = 0x2;
    public final static int AcpiReclaim = 0x3;
    public final static int AcpiNvs = 0x4;
  }

  public static class SystemMemoryMapAddressRangeDescriptor extends STRUCT {
    public long baseAddress;
    public long lengthInBytes;
    public int typeOfAddressRange;
  }
  
  public static void printSystemMemoryMap() {
    // continuation must be 0 for first call
    int continuation = 0;

    do {
      // size always 20
      callSetSystemMemoryMap(continuation, 20);

      // error
      if ((BIOS.regs.FLAGS & BIOS.CF_MASK) != 0)
        Kernel.panic(ErrorCode.BiosCallFailed);

      continuation = BIOS.regs.EBX;

      SystemMemoryMapAddressRangeDescriptor descriptor =
        (SystemMemoryMapAddressRangeDescriptor)MAGIC.cast2Struct(BIOS.BIOS_MEMMAP_START);
      
      printDescriptor(descriptor);
    } while (continuation != 0); // we are finished if continuation (EBX) is 0
  }

  public static String mapAddressTypeToString(int type) {
    switch (type) {
      case SystemMemoryMapAddressType.AvailableToOs:
        return "AvailableToOs";
      case SystemMemoryMapAddressType.Reserved:
        return "Reserved";
      case SystemMemoryMapAddressType.AcpiReclaim:
        return "ACPI Reclaim";
      case SystemMemoryMapAddressType.AcpiNvs:
        return "ACPI NVS";
    
      default: return "Undefined";
    }
  }

  private static void callSetSystemMemoryMap(int continuation, int size) {
    BIOS.regs.EAX = 0xE820;
    BIOS.regs.EDX = 0x534D4150; // 'SMAP'
    BIOS.regs.EBX = continuation;

    /*
      A F F E 0 -> & 0xF for right 0
    +   C A F E -> >>> 4
      B C A D E
    */
    BIOS.regs.EDI = BIOS.BIOS_MEMMAP_START & 0xF;
    BIOS.regs.ES = (short)(BIOS.BIOS_MEMMAP_START >>> 4);

    BIOS.regs.ECX = size; // size in bytes

    BIOS.rint(0x15);
  }

  private static void printDescriptor(SystemMemoryMapAddressRangeDescriptor descriptor) {
    Console.print("0x");
    Console.printHex(descriptor.baseAddress, (byte)7);
    Console.print(" - 0x");
    Console.printHex(descriptor.baseAddress + descriptor.lengthInBytes, (byte)7);
    Console.print("  ");
    Console.print(mapAddressTypeToString(descriptor.typeOfAddressRange));
    Console.print('\n');
  }
}