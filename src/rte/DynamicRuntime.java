package rte;

import kernel.screen.Screen;
// import java.lang.MAGIC;

public class DynamicRuntime {
  public static Object newInstance(int scalarSize, int relocEntries, SClassDesc type) {
    // 16 bytes after imageBase is the address of the first object
    int firstObjectAddress = MAGIC.rMem32(MAGIC.imageBase + 16);
    Object firstObject = MAGIC.cast2Obj(firstObjectAddress);

    // walk the list to get to the last object
    Object lastObject = firstObject;
    while (lastObject._r_next != null) {
        lastObject = lastObject._r_next;
    }

    int lastObjectAddress = MAGIC.cast2Ref(lastObject);

    // 2 * 4 -> skip _r_relocEntries and _r_scalarSize
    // lastObject._r_scalarSize -> point to first byte after lastObject
    int newObjectStartAddress = lastObjectAddress + 2 * 4 + lastObject._r_scalarSize; 

    // 2 * 4 -> skip _r_next and _r_type to point to _r_relocEntries
    int newObjectAddress = newObjectStartAddress + relocEntries * 4 + 2 * 4;

    // align the scalar size and thus the object to a multiple of 4 bytes
    int alignedScalarSize = scalarSize;
    if (alignedScalarSize % 4 != 0) alignedScalarSize += alignedScalarSize % 4;

    // points to first byte after newObject
    // 2 * 4 -> skip _r_relocEntries and _r_scalarSize
    int newObjectEndAddress = newObjectAddress + 2 * 4 + alignedScalarSize;

    // set everything for newObject to zero
    for (int address = newObjectStartAddress;
         address < newObjectEndAddress;
         address += 4) MAGIC.wMem32(address, 0);

    Object newObject = MAGIC.cast2Obj(newObjectAddress);
    MAGIC.assign(newObject._r_relocEntries, relocEntries);
    MAGIC.assign(newObject._r_scalarSize, scalarSize);
    // MAGIC.assign(newObject._r_next, null); // should already be null
    MAGIC.assign(newObject._r_type, type);

    MAGIC.assign(lastObject._r_next, newObject);

    return lastObject;

    // test some assumptions
    // int reloc = MAGIC.rMem32(lastObjectAddress);
    // Screen.print("\nRelocEntries: ");
    // Screen.printInteger(reloc);
    // Screen.print("\n");
    // Screen.printInteger(lastObject._r_relocEntries);
    // Screen.print("\n\n");

    // int scalar = MAGIC.rMem32(lastObjectAddress + 4);
    // Screen.print("\nScalarSize: ");
    // Screen.printInteger(scalar);
    // Screen.print("\n");
    // Screen.printInteger(lastObject._r_scalarSize);
    // Screen.print("\n\n");
  }
      
  public static SArray newArray(int length, int arrDim, int entrySize,
      int stdType, Object unitType) { while(true); }
  public static void newMultArray(SArray[] parent, int curLevel,
      int destLevel, int length, int arrDim, int entrySize, int stdType,
      Object unitType) { while(true); }
  public static boolean isInstance(Object o, SClassDesc dest,
      boolean asCast) { while(true); }
  public static SIntfMap isImplementation(Object o, SIntfDesc dest,
      boolean asCast) { while(true); }
  public static boolean isArray(SArray o, int stdType,
      Object unitType, int arrDim, boolean asCast) { while(true); }
  public static void checkArrayStore(Object dest,
      SArray newEntry) { while(true); }

  // private static void memset(int startAddress, int endAddress, byte value) {
  //   int byteCount = endAddress - startAddress;
  //   int longCount = byteCount / 8;
  //   byteCount -= longCount * 8;

  //   for (int i = 0; i < longCount; i++) MAGIC.wMem64(startAddress + i * 8, value);
  //   for (int i = 0; i < byteCount; i++) MAGIC.wMem8(startAddress + longCount * 8 + i, value);
  // }

  // private static void memset(int startAddress, int endAddress, byte value) {
  //   // only works if startAddress is aligned

  //   int endAligned = endAddress & ~0x;

  //   for 
  // }
}
