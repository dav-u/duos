package kernel;

import kernel.io.console.Console;
import kernel.io.keyboard.KeyCode;
import kernel.io.keyboard.Keyboard;
import rte.SClassDesc;
import rte.instancing.EmptyObject;
import rte.instancing.GarbageCollectingInstanceCreator;
import kernel.time.Timer;

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

    // walk through the linked list of objects starting from 'object'
    while (current != null) {
      // current is marked or an EmptyObject, let's look at the next one
      if (isMarked(current) || current instanceof EmptyObject) {
        Console.print('M');
        prev = current;
        current = current._r_next;
        continue;
      }
      Console.print('N');

      Object nextObject = current._r_next;

      Console.print("Address of current: ");
      Console.printHex(MAGIC.cast2Ref(current));
      Console.print('\n');
      // object is not marked => nobody uses this object
      Console.print("Press...");
      Keyboard.waitFor(KeyCode.Enter);
      
      // create emptyObject in place of unused object
      int startAddress = Memory.startAddress(current);
      int endAddress = Memory.addressAfter(current);

      EmptyObject emptyObject = EmptyObject.createIn(startAddress, endAddress);

      // link empty object into same place as the unused 
      MAGIC.assign(emptyObject._r_next, nextObject);
      MAGIC.assign(prev._r_next, (Object)emptyObject);

      // emptyObject replaced current so now it must become prev
      prev = emptyObject;
      current = nextObject;

      // Object cur = GarbageCollectingInstanceCreator.firstDynamicObject;
      // while (cur != null) {
      //   cur = cur._r_next;
      //   int addr = MAGIC.cast2Ref(cur);
      //   if (addr < 100) {
      //     Console.print("Found the culprit\n");
      //     Console.printHex(addr);
      //     while(true);
      //   }
      // }
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
      if (referencedObject == null) continue;

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