package kernel.hardware.ac97;

public class NamRegisters {
  // Reset Register
  public static final int RESET_REGISTERS = 0x00;

  // Capabilities
  public static final int CAPABILITIES = 0x00;

  // Set Master Output Volume
  public static final int SET_MASTER_OUTPUT_VOLUME = 0x02;

  // Set AUX Output Volume
  public static final int SET_AUX_OUTPUT_VOLUME = 0x04;

  // Set Microphone Volume
  public static final int SET_MICROPHONE_VOLUME = 0x0E;

  // Set PCM Output Volume
  public static final int SET_PCM_OUTPUT_VOLUME = 0x18;

  // Select Input Device
  public static final int SELECT_INPUT_DEVICE = 0x1A;

  // Set Input Gain
  public static final int SET_INPUT_GAIN = 0x1C;

  // Set Gain of Microphone
  public static final int SET_GAIN_OF_MICROPHONE = 0x1E;

  // Extended capabilities
  public static final int EXTENDED_CAPABILITIES = 0x28;

  // Control of extended capabilities
  public static final int CONTROL_OF_EXTENDED_CAPABILITIES = 0x2A;

  // Sample rate of PCM Front DAC
  public static final int SAMPLE_RATE_PCM_FRONT_DAC = 0x2C;

  // Sample rate of PCM Surr DAC
  public static final int SAMPLE_RATE_PCM_SURR_DAC = 0x2E;

  // Sample rate of PCM LFE DAC
  public static final int SAMPLE_RATE_PCM_LFE_DAC = 0x30;

  // Sample rate of PCM L/R ADC
  public static final int SAMPLE_RATE_PCM_LR_ADC = 0x32;
}