package java.lang;

import kernel.GarbageCollector;
import rte.SClassDesc;

public class Object {
  public final SClassDesc _r_type=null;
  public final Object _r_next=null;
  public final int _r_relocEntries=0, _r_scalarSize=0;
  public int flags = 0;

  public String getClassName() {
    return _r_type.name;
  }
}