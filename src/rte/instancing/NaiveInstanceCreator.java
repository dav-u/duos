package rte.instancing;

import rte.SClassDesc;
import java.lang.Bits;

public class NaiveInstanceCreator {
  private static Object lastObject = null;
  private static Object firstDynamicObject = null;

  public static void init() {
    // 16 bytes after imageBase is the address of the first object
    int firstObjectAddress = MAGIC.rMem32(MAGIC.imageBase + 16);
    Object firstObject = MAGIC.cast2Obj(firstObjectAddress);

    // walk the list to get to the last object
    lastObject = firstObject;
    while (lastObject._r_next != null) {
      lastObject = lastObject._r_next;
    }

    firstDynamicObject = new Object();
  }

  public static Object newInstance(int scalarSize, int relocEntries, SClassDesc type) {
    int lastObjectAddress = MAGIC.cast2Ref(lastObject);

    // lastObject._r_scalarSize -> point to first byte after lastObject
    int newObjectStartAddress = lastObjectAddress + lastObject._r_scalarSize;

    // align start address
    newObjectStartAddress = Bits.alignToMultipleOf4Up(newObjectStartAddress);

    // 2 * 4 -> skip _r_next and _r_type to point to _r_relocEntries
    int newObjectAddress = newObjectStartAddress + relocEntries * 4;

    // points to first byte after newObject
    int newObjectEndAddress = newObjectAddress + scalarSize;

    // align new object end address
    newObjectEndAddress = Bits.alignToMultipleOf4Up(newObjectEndAddress);

    // set everything for newObject to zero
    for (int address = newObjectStartAddress; address < newObjectEndAddress; address += 4)
      MAGIC.wMem32(address, 0);

    Object newObject = MAGIC.cast2Obj(newObjectAddress);
    MAGIC.assign(newObject._r_relocEntries, relocEntries);
    MAGIC.assign(newObject._r_scalarSize, scalarSize);
    // MAGIC.assign(newObject._r_next, null); // should already be null
    MAGIC.assign(newObject._r_type, type);

    MAGIC.assign(lastObject._r_next, newObject);

    lastObject = newObject;

    return newObject;
  }
}