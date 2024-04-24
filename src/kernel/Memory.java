package kernel;

public class Memory {
  public final static int StackStart = 0x9BFFC;

  public static int addressAfter(Object object) {
    return MAGIC.cast2Ref(object) + object._r_scalarSize;
  }
}