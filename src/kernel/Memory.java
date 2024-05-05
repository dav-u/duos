package kernel;

public class Memory {
  public final static int StackStart = 0x9BFFC;

  public final static int BIOS_MEMORY = 0x60000;
  public final static int BIOS_STKEND = BIOS_MEMORY+0x1000;
  public final static int BIOS_STKBSE = BIOS_STKEND-0x28;

  // Put system memory map buffer before bios memory.
  // Needs to be below 1MB. Otherwise BIOS can't access it.
  public final static int BIOS_MEMMAP_START = BIOS_MEMORY - 32;

  public final static int SCRATCH = BIOS_MEMMAP_START - 2048; // TODO: is 2048 enough/correct? I think 256 is enough...

  public final static int OFFSET_FIRST_HEAP_OBJECT = 16;

  public static int addressAfter(Object object) {
    return MAGIC.cast2Ref(object) + object._r_scalarSize;
  }

  public static int startAddress(Object object) {
    return MAGIC.cast2Ref(object) - object._r_relocEntries * 4;
  }

  public static Object getFirstHeapObject() {
    // 16 bytes after imageBase is the address of the first object
    int firstObjectAddress = MAGIC.rMem32(MAGIC.imageBase + OFFSET_FIRST_HEAP_OBJECT);
    Object firstObject = MAGIC.cast2Obj(firstObjectAddress);

    return firstObject;
  }
}