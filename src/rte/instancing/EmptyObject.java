import kernel.ErrorCode;
import kernel.Kernel;
import rte.SClassDesc;

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