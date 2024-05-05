package rte.instancing;

import kernel.ErrorCode;
import kernel.Kernel;
import kernel.io.console.Console;
import rte.SClassDesc;

/*
 * Cannot be larger than Object!
 */
public class EmptyObject {
  public int freeSpace() {
    return this._r_scalarSize - scalarSize();
  }

  public int shrink(int space) {
    // this should not happen
    if (space > freeSpace())
      Kernel.panic(ErrorCode.OutOfMemory, "Requested empty object to shrink more than its free space");
    
    // reduce the scalar size
    MAGIC.assign(this._r_scalarSize, this._r_scalarSize - space);

    return MAGIC.cast2Ref(this) + this._r_scalarSize;
  }

  public void fuseWith(EmptyObject emptyObject) {
    // TODO
  }

  /*
   * startAddress is inclusive, endAddress exclusive.
   * Does not set next pointer.
   */
  public static EmptyObject createIn(int startAddress, int endAddress) {
    int freeSpace = endAddress - startAddress - relocEntryCount()*4 - scalarSize();

    if (freeSpace < 0)
      Kernel.panic(ErrorCode.OutOfMemory, "Could not create empty object in specified region. Not enough space.");

    Object newObject = GarbageCollectingInstanceCreator.newInstanceAt(
      startAddress,
      relocEntryCount(),
      freeSpace,
      classDesc());
    
    return (EmptyObject)newObject;
  }

  @SJC.Inline
  public static int relocEntryCount() {
    return MAGIC.getInstRelocEntries("EmptyObject");
  }

  @SJC.Inline
  public static int scalarSize() {
    return MAGIC.getInstScalarSize("EmptyObject");
  }

  @SJC.Inline
  public static SClassDesc classDesc() {
    return MAGIC.clssDesc("EmptyObject");
  }
}