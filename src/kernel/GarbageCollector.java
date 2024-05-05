package kernel;

import kernel.io.console.Console;
import rte.SClassDesc;
import rte.instancing.EmptyObject;
import rte.instancing.GarbageCollectingInstanceCreator;

/*
 * Does a mark and sweep over all objects in the heap.
 */
public class GarbageCollector {
  public static int unmarkedFlag = 0;

  public static void run() {
    Object firstDynamicObject = GarbageCollectingInstanceCreator.firstDynamicObject;
    Object rootObject = Memory.getFirstHeapObject();

    while (rootObject != null) {
      markObjectAndReferencedObjects(rootObject);
      rootObject = rootObject._r_next;
    }

    sweepFrom(firstDynamicObject);
    // fuseEmptyObjectsFrom(firstDynamicObject);

    // switch marked and unmarked
    if (unmarkedFlag == 0) unmarkedFlag = 1;
    else unmarkedFlag = 0;
  }

  /*
   * Sweeps the objects that are not marked (replaces them with EmptyObjects).
   * Assumes that 'object' is marked.
   */
  private static void sweepFrom(Object object) {
    Object current = object;
    Object prev = null;

    while (true) {
      // object is marked, let's look at the next one
      if (isMarked(current) || current instanceof EmptyObject) {
        prev = current;
        current = current._r_next;
        continue;
      }
      
      Object nextObject = current._r_next;

      // object is not marked => nobody uses this object
      
      // create emptyObject in place of unused object
      EmptyObject emptyObject = EmptyObject.createIn(
        Memory.startAddress(current),
        Memory.addressAfter(current));
      
      // link empty object into same place as the unused 
      MAGIC.assign(emptyObject._r_next, (Object)nextObject);
      MAGIC.assign(prev._r_next, (Object)emptyObject);
    }
  }

  private static String DEBUG_getClassName(Object object) {
    if (object instanceof byte[]) return "byte[]";
    if (object instanceof long[]) return "long[]";

    else return "Unknown";
  }

  private static void markObjectAndReferencedObjects(Object object) {
    if (isMarked(object)) return;

    mark(object);

    int fanOut = fanOut(object);

    // if (fanOut > 0) {
    //   Console.print("Marked at ");
    //   Console.print(MAGIC.cast2Ref(object));

    //   Console.print("Fan out: ");
    //   Console.print(fanOut);
    //   Console.print("   ");
    // }

    for (int i = 0; i < fanOut; i++) {
      Object referencedObject = getNthReference(object, i);
      markObjectAndReferencedObjects(referencedObject);
    }
  }

  private static boolean isMarked(Object object) {
    return (object.flags & 0x1) != unmarkedFlag;
  }

  private static void mark(Object object) {
    int markedFlag = 1 - unmarkedFlag;
    object.flags = (object.flags & ~1) | markedFlag;
  }

  /*
   * referenceIndex = 0 returns the first reference
   */
  // TODO make private
  public static Object getNthReference(Object object, int referenceIndex) {
    int objectAddress = MAGIC.cast2Ref(object);
    // Console.print("address:");
    // Console.printHex(objectAddress, (byte)7);
    // Console.print('\n');
    int referenceAddress = objectAddress - 3*4 - referenceIndex*4;
    // Console.print("reference:");
    // Console.printHex(referenceAddress, (byte)7);
    // Console.print('\n');

    int reference = MAGIC.rMem32(referenceAddress);

    return MAGIC.cast2Obj(reference);
  }

  // TODO make private
  public static int fanOut(Object object) {
    return object._r_relocEntries - 2;
  }
}