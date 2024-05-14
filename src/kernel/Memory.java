package kernel;

import java.lang.Bits;

public class Memory {
  public final static int StackStart = 0x9BFFC;

  public final static int BIOS_MEMORY = 0x60000;
  public final static int BIOS_STKEND = BIOS_MEMORY+0x1000;
  public final static int BIOS_STKBSE = BIOS_STKEND-0x28;

  // Put system memory map buffer before bios memory.
  // Needs to be below 1MB. Otherwise BIOS can't access it.
  public final static int BIOS_MEMMAP_START = BIOS_MEMORY - 32;

  public final static int SCRATCH = BIOS_MEMMAP_START - 2048; // TODO: is 2048 enough/correct? I think 256 is enough...

  // TODO: do I really need to align the tables themselves?
  /*
   * Make space for the page directory and two page tables
   * (size of one of them is 1024 entries * 4 byte entry size; with one PD and two PT
   *  wee need 3). Also page directories and tables **must** be aligned to a multiple
   * of 4096.
   */
  public final static int PAGE_DIRECTORY_START = (SCRATCH - 1024 * 4 * 3) & ~(4096 - 1);

  /*
   * Page table low comes directly after the page directory in memory.
   */
  public final static int PAGE_TABLE_LOW_START = PAGE_DIRECTORY_START + 1024 * 4;

  /*
   * Page table high comes directly after the page table low in memory.
   */
  public final static int PAGE_TABLE_HIGH_START = PAGE_TABLE_LOW_START + 1024 * 4;

  public final static int OFFSET_FIRST_HEAP_OBJECT = 16;

  public static int addressAfter(Object object) {
    return MAGIC.cast2Ref(object) + object._r_scalarSize;
  }

  public static int startAddress(Object object) {
    return MAGIC.cast2Ref(object) - object._r_relocEntries * 4;
  }

  public static Object getFirstHeapObject() {
    // 16 bytes after imageBase is the address of the first object
    int firstObjectAddress = MAGIC.rMem32(MAGIC.imageBase + OFFSET_FIRST_HEAP_OBJECT);
    Object firstObject = MAGIC.cast2Obj(firstObjectAddress);

    return firstObject;
  }

  public static void initializeVirtualMemory() {
    // TODO: what does read/write do for these?
    int pageDirectoryEntryLow = build4KBPageEntry(PAGE_TABLE_LOW_START, true);
    int pageDirectoryEntryHigh = build4KBPageEntry(PAGE_TABLE_HIGH_START, true);
    MAGIC.wMem32(PAGE_DIRECTORY_START + 0 * 4, pageDirectoryEntryLow);
    MAGIC.wMem32(PAGE_DIRECTORY_START + 1023 * 4, pageDirectoryEntryHigh);

    // 1024 entries, first and last one have been set ->
    // set 1 to 1022
    int fourMb = 1024*1024*4;
    for (int i = 1; i <= 1022; i++) {
      int address = i * fourMb;
      int pageDirectoryEntry = build4MBPageEntry(address, true);
      MAGIC.wMem32(PAGE_DIRECTORY_START + i * 4, pageDirectoryEntry);
    }

    // now we have initialized the page directory. Next the two page tables.

    // not writable at address 0 (catch null pointers)
    int pageTableEntryLow = build4KBPageEntry(0, false);
    MAGIC.wMem32(PAGE_TABLE_LOW_START + 0 * 4, pageTableEntryLow);

    // last page is the last 4096 bytes of 4GB.
    int lastPageAddress = fourMb * 1024 - 4096;
    int pageTableEntryHigh = build4KBPageEntry(lastPageAddress, false);

    MAGIC.wMem32(PAGE_TABLE_HIGH_START + 1023 * 4, pageTableEntryHigh);

    int fourKb = 1024 * 4;
    for (int i = 1; i < 1024; i++) {
      int address = i * fourKb;
      boolean isWritable = true;
      int pageTableEntry = build4KBPageEntry(address, isWritable);
      MAGIC.wMem32(PAGE_TABLE_LOW_START + i * 4, pageTableEntry);
    }

    int offset = fourMb * 1023;
    for (int i = 0; i < 1023; i++) {
      int address = i * fourKb + offset;
      boolean isWritable = true;
      int pageTableEntry = build4KBPageEntry(address, isWritable);
      MAGIC.wMem32(PAGE_TABLE_HIGH_START + i * 4, pageTableEntry);
    }

    enablePageSizeBit();
    setCR3(PAGE_DIRECTORY_START);
    enableMmu();
  }

  public static int build4MBPageEntry(int address, boolean isWritable) {
    // we should never trigger this check
    if (address % 1024*1024*4 != 0)
      Kernel.panic(ErrorCode.VirtualMemory, "Tried to use address in 4MB entry that is not aligned to a multiple of 4MB");
    
    int pageEntry = address;

    // Page Size 0 -> 4MB
    pageEntry |= 1 << 7;
    // Read/Write 0 -> readonly; 1 -> read and write
    pageEntry |= Bits.boolToBit(isWritable) << 1;
    // Present 1 -> Yes, page is present
    pageEntry |= 1 << 0;

    return pageEntry;
  }

  public static int build4KBPageEntry(int address, boolean isWritable) {
    // we should never trigger this check
    if (address % 4096 != 0)
      Kernel.panic(ErrorCode.VirtualMemory, "Tried to use address in 4KB entry that is not aligned to a multiple of 4KB");
    
    int pageEntry = address;

    // Page Size 0 -> 4KB
    pageEntry |= 0 << 7;
    // Read/Write 0 -> readonly; 1 -> read and write
    pageEntry |= Bits.boolToBit(isWritable) << 1;
    // Present 1 -> Yes, page is present
    pageEntry |= 1 << 0;

    return pageEntry;
  }

  public static void enableMmu() {
    MAGIC.inline(0x0F, 0x20, 0xC0); //mov eax,cr0
    MAGIC.inline(0x0D, 0x00, 0x00, 0x01, 0x80); //or eax,0x80010000
    MAGIC.inline(0x0F, 0x22, 0xC0); //mov cr0,eax
  }

  public static int getCR2() {
    int cr2=0;
    MAGIC.inline(0x0F, 0x20, 0xD0); //mov e/rax,cr2
    MAGIC.inline(0x89, 0x45); MAGIC.inlineOffset(1, cr2); //mov [ebp-4],eax
    return cr2;
  }

  public static void setCR3(int addr) {
    MAGIC.inline(0x8B, 0x45); MAGIC.inlineOffset(1, addr); //mov eax,[ebp+8]
    MAGIC.inline(0x0F, 0x22, 0xD8); //mov cr3,eax
  }

  public static void enablePageSizeBit() {
    MAGIC.inline(0x0F, 0x20, 0xE0); //mov eax,cr4
    MAGIC.inline(0x83, 0xC8, 0x10); //or eax,0x00000010
    MAGIC.inline(0x0F, 0x22, 0xE0); //mov cr4,eax
  }
}