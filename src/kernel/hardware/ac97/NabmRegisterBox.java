package kernel.hardware.ac97;

public class NabmRegisterBox {
    // Physical Address of Buffer Descriptor List (dword)
    public static final int BUFFER_DESCRIPTOR_LIST_ADDRESS = 0x00;

    // Number of Actual Processed Buffer Descriptor Entry (byte)
    public static final int NUM_ACTUAL_PROCESSED_DESCRIPTOR_ENTRY = 0x04;

    // Number of all Descriptor Entries (byte)
    public static final int ENTRY_COUNT = 0x05;

    // Status of transferring Data (word)
    public static final int STATUS_TRANSFERRING_DATA = 0x06;

    // Number of transferred Samples in Actual Processed Entry (word)
    public static final int NUM_TRANSFERRED_SAMPLES_ACTUAL_PROCESSED_ENTRY = 0x08;

    // Number of next processed Buffer Entry (byte)
    public static final int NUM_NEXT_PROCESSED_BUFFER_ENTRY = 0x0A;

    // Transfer Control (byte)
    public static final int TRANSFER_CONTROL = 0x0B;
}