package rte.instancing;

import rte.SClassDesc;
import kernel.Memory;
import kernel.Kernel;
import kernel.ErrorCode;
import java.lang.Bits;

public class GarbageCollectingInstanceCreator {
  public static Object lastObject = null;
  public static Object firstDynamicObject = null;

  public static void init() {
    Object firstObject = Memory.getFirstHeapObject();

    // walk the list to get to the last object
    lastObject = firstObject;
    while (lastObject._r_next != null) {
      lastObject = lastObject._r_next;
    }

    int emptyObjectStartAddress = Bits.alignToMultipleOf4Up(Memory.addressAfter(lastObject));
    int maxHeapAddress = Bits.alignToMultipleOf4Down(kernel.bios.SystemMemoryMap.getHeapMaxAddress());
    int availableSpace = maxHeapAddress - emptyObjectStartAddress;

    Object emptyObjectAsObject = newInstanceAt(
      emptyObjectStartAddress,
      availableSpace,
      EmptyObject.relocEntryCount(),
      EmptyObject.classDesc());

    // The created object is intentionally not linked to the previous objects.
    // Later we consider the heap objects created by the compiler as the root set for
    // the garbage collection and thus need a seperation.
    lastObject = firstDynamicObject = emptyObjectAsObject;
  }

  public static Object newInstance(int scalarSize, int relocEntries, SClassDesc type) {
    // find empty object
    EmptyObject emptyObject = findEmptyObjectStartingFrom(firstDynamicObject, true);
    
    // search for empty object that can fit our new object
    while (true) {
      if (emptyObject == null)
        Kernel.panic(ErrorCode.OutOfMemory, "Could not find fitting empty object");

      int requiredSpace = requiredSpace(scalarSize, relocEntries);

      if (emptyObject.freeSpace() >= requiredSpace) {
        // shrink empty object to create space for the new object
        int startAddress = emptyObject.shrink(requiredSpace);

        Object newObject = newInstanceAt(
          startAddress,
          scalarSize,
          relocEntries,
          type);
        
        MAGIC.assign(lastObject._r_next, newObject);
        lastObject = newObject;

        return newObject;
      }

      // search for next empty object
      emptyObject = findEmptyObjectStartingFrom(emptyObject, false);
    }
  }

  /*
   * Creates a new instance at the specified address.
   * Does *not* set _r_next.
   */
  public static Object newInstanceAt(
    int startAddress,
    int scalarSize,
    int relocEntries,
    SClassDesc type) {

    // skip reloc entries to point to object address
    int address = startAddress + relocEntries * 4;

    int requiredSpace = requiredSpace(scalarSize, relocEntries);
    
    // set memory to zero
    for (int i = 0; i < requiredSpace; i+=4)
      MAGIC.wMem32(startAddress+i, 0);

    Object newObject = MAGIC.cast2Obj(address);

    MAGIC.assign(newObject._r_relocEntries, relocEntries);
    MAGIC.assign(newObject._r_scalarSize, scalarSize);
    // MAGIC.assign(newObject._r_next, null); // should already be null through memset
    MAGIC.assign(newObject._r_type, type);

    return newObject;
  }

  /*
   * Finds an object of type EmptyObject by traversing the linked list of objects.
   * Starts with 'startObject'.
   * Can return 'startObject' when 'includeStartObject' is set.
   */
  private static EmptyObject findEmptyObjectStartingFrom(Object startObject, boolean includeStartObject) {
    Object currentObject = includeStartObject ? startObject : startObject._r_next;
    while (!(currentObject instanceof EmptyObject)) {
      currentObject = currentObject._r_next;
      if (currentObject == null)
        return null;
    }

    return (EmptyObject)currentObject;
  }

  /*
   * Required space for an object with scalarSize and relocEntries.
   */
  private static int requiredSpace(int scalarSize, int relocEntries) {
    return Bits.alignToMultipleOf4Up(scalarSize) + relocEntries * 4;
  }
}