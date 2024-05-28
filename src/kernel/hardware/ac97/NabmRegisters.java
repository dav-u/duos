package kernel.hardware.ac97;

public class NabmRegisters {
    // NABM register box for PCM IN
    public static final int NABM_REGISTER_PCM_IN = 0x00;

    // NABM register box for PCM OUT
    public static final int NABM_REGISTER_PCM_OUT = 0x10;

    // NABM register box for Microphone
    public static final int NABM_REGISTER_MICROPHONE = 0x20;

    // Global Control Register
    public static final int GLOBAL_CONTROL_REGISTER = 0x2C;

    // Global Status Register
    public static final int GLOBAL_STATUS_REGISTER = 0x30;
}