package kernel;

import kernel.interrupt.Interrupts;
import kernel.io.console.Console;
import kernel.io.keyboard.*;
import rte.SClassDesc;
import rte.instancing.EmptyObject;
import rte.instancing.GarbageCollectingInstanceCreator;
import kernel.time.Timer;
import kernel.scheduler.Scheduler;

/*
 * Does a mark and sweep over all objects in the heap.
 */
public class GarbageCollector {
  public static final int markedFlag = 0x1;

  public static void run() {
    //Interrupts.preventPicInterrupts();
    //Kernel.checkDynamicObjects("Before GC.run");
    Object firstDynamicObject = GarbageCollectingInstanceCreator.firstDynamicObject;
    Object rootObject = Memory.getFirstHeapObject();

    resetMarkFrom(rootObject);
    resetMarkFrom(firstDynamicObject);

    while (rootObject != null) {
      markObjectAndReferencedObjects(rootObject);
      rootObject = rootObject._r_next;
    }

    Keyboard.keyMap.printKeyGCFlags();

    Kernel.checkDynamicObjects("After GC.mark");

    sweepFrom(firstDynamicObject);

    Kernel.checkDynamicObjects("After GC.sweep");
    // fuseEmptyObjectsFrom(firstDynamicObject);

    //Interrupts.restorePicInterrupts();
  }

  private static void resetMarkFrom(Object start) {
    Object current = start;
    while (current != null) {
      unmark(current);
      current = current._r_next;
    }
  }

  /*
   * Sweeps the objects that are not marked (replaces them with EmptyObjects).
   * Assumes that 'object' is marked.
   */
  private static void sweepFrom(Object object) {
    Object current = object;
    Object prev = null;
    int count = 1;

    // walk through the linked list of objects starting from 'object'
    while (current != null) {
      // if current is marked or an EmptyObject, let's look at the next one
      if (isMarked(current) || current instanceof EmptyObject) {
        //Console.print('M');
        prev = current;
        current = current._r_next;
        count++;
        continue;
      }
      // Console.clear();

      Object nextObject = current._r_next;

      // create emptyObject in place of unused object
      int startAddress = Memory.startAddress(current);
      int endAddress = Memory.addressAfter(current);

      Kernel.checkDynamicObjects("before EmptyObject.createIn");
      Console.print(DEBUG_getClassName(current));
      Console.print(count);

      EmptyObject emptyObject = EmptyObject.createIn(startAddress, endAddress);

      Keyboard.keyMap.printKeyGCFlags();

      // Kernel.checkDynamicObjects("after EmptyObject.createIn");

      // link empty object into same place as the unused 
      MAGIC.assign(emptyObject._r_next, nextObject);
      MAGIC.assign(prev._r_next, (Object)emptyObject);

      Console.print("\nAddress of emptyObject: ");
      Console.printHex(MAGIC.cast2Ref(emptyObject));

      Kernel.checkDynamicObjects("after EmptyObject.createIn");

      // emptyObject replaced current so now it must become prev
      prev = emptyObject;
      current = nextObject;

      Kernel.checkDynamicObjects("end of sweep loop");

      count++;
    }
  }

  /*
   * Small hack to get the type of the object.
   * RTE symbolinformation (-u rte) did sadly not fit into image.
   */
  private static String DEBUG_getClassName(Object object) {
    if (object instanceof byte[]) return "byte[]";
    if (object instanceof long[]) return "long[]";
    if (object instanceof Scheduler) return "Scheduler";
    if (object instanceof Key) return "Key";
    if (object instanceof KeyEvent) return "KeyEvent";
    if (object instanceof EmptyObject) return "EmptyObject";
    if (object instanceof KeyMap) return "KeyMap";
    if (object instanceof boolean[]) return "boolean[]";

    else return "Unknown";
  }

  private static void markObjectAndReferencedObjects(Object object) {
    if (!(object instanceof Object)) return;
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
    return (object.flags & markedFlag) != 0;
  }

  private static void mark(Object object) {
    object.flags = (object.flags & ~markedFlag) | markedFlag;
  }

  private static void unmark(Object object) {
    object.flags = (object.flags & ~markedFlag);
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