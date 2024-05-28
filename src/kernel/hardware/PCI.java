package kernel.hardware;

import kernel.io.console.Console;
import kernel.io.console.SymbolColor;

/*
 * This class allows to read from the PCI Bus and set certain values for devices.
 * This class implements an iterator. Call resetIterator() to start iterating.
 */
public class PCI {
  public static int bus = 0;
  public static int device = 0;
  public static int function = 0;

  private final static int busSize = 3;
  private final static int deviceSize = 32;
  private final static int functionSize = 8;

  private final static int addressPort = 0x0CF8;
  private final static int dataPort = 0x0CFC;

  public static class BaseClassCode {
    public final static int OldDevice = 0x00;
    public final static int SystemPeripheral = 0x08;
    public final static int MassStorage = 0x01;
    public final static int InputDevice = 0x09;
    public final static int NetworkController = 0x02;
    public final static int DockingStation = 0x0A;
    public final static int DisplayController = 0x03;
    public final static int ProcessorUnit = 0x0B;
    public final static int MultimediaDevice = 0x04;
    public final static int SerialBus = 0x0C;
    public final static int MemoryController = 0x05;
    public final static int WirelessCommunicationDevice = 0x0D;
    public final static int Bridge = 0x06;
    public final static int IntelligentController = 0x0E;
    public final static int CommunicationController = 0x07;
    public final static int SatelliteCommunication = 0x0F;

    public static String convertToString(int baseClassCode) {
      switch (baseClassCode) {
        case BaseClassCode.OldDevice: return "OldDevice";
        case BaseClassCode.SystemPeripheral: return "SystemPeripheral";
        case BaseClassCode.MassStorage: return "MassStorage";
        case BaseClassCode.InputDevice: return "InputDevice";
        case BaseClassCode.NetworkController: return "NetworkController";
        case BaseClassCode.DockingStation: return "DockingStation";
        case BaseClassCode.DisplayController: return "DisplayController";
        case BaseClassCode.ProcessorUnit: return "ProcessorUnit";
        case BaseClassCode.MultimediaDevice: return "MultimediaDevice";
        case BaseClassCode.SerialBus: return "SerialBus";
        case BaseClassCode.MemoryController: return "MemoryController";
        case BaseClassCode.WirelessCommunicationDevice: return "WirelessCommunicationDevice";
        case BaseClassCode.Bridge: return "Bridge";
        case BaseClassCode.IntelligentController: return "IntelligentController";
        case BaseClassCode.CommunicationController: return "CommunicationController";
        case BaseClassCode.SatelliteCommunication: return "SatelliteCommunication";
        default: return "Unkown";
      }
    }
  }

  public static void disableIoAndMemoryDecode() {
    //Before attempting to read the information about the BAR, make sure to disable both I/O and memory decode in the command byte - OSDev
    
    int register = 1; // register with command in it
    int address = buildAddress(bus, device, function, register);

    MAGIC.wIOs32(addressPort, address);
    int reg2 = MAGIC.rIOs32(dataPort);

    reg2 &= ~(1<<1); // clear bit 1 -> disable memory space
    reg2 &= ~(1<<0); // clear bit 0 -> disable IO space

    // not sure if this is needed again, but better safe than sorry (some IO ports behave strange...)
    MAGIC.wIOs32(addressPort, address);

    MAGIC.wIOs32(dataPort, reg2);
  }

  public static void writeReg(int reg, int value) {
    MAGIC.wIOs32(addressPort, buildAddress(bus, device, function, reg));
    MAGIC.wIOs32(dataPort, value);
  }

  public static int readReg(int reg) {
    MAGIC.wIOs32(addressPort, buildAddress(bus, device, function, reg));
    return MAGIC.rIOs32(dataPort);
  }

  public static void resetIterator() {
    bus = 0;
    device = 0;
    function = -1;
  }

  /*
   * Selects the next function (or next device or bus)
   */
  public static boolean next() {
    // search for next valid device
    do {
      // TODO: has the device set the 0x00800000 flag only
      // for the first function of a multi-function device?
      // -> we currently assume it has it set for all subsequent
      //    functions as well

      // if we are not a multi-function device we need to skip to the
      // next device. Otherwise look at the next function
      if (function != -1 && isMultiFunctionDevice())
        function = functionSize; // this selects the next device
      else function++;

      if (function >= functionSize) {
        function = 0;
        device++;
      }

      if (device >= deviceSize) {
        device = 0;
        bus++;
      }

      if (bus >= busSize) return false; // we stop our search after we reach busSize
    } while(readReg(0) == 0 || readReg(0) == -1); // loop as long as no valid device is found

    return true;
  }

  public static void printDevices() {
    Console.print("Devices:\n");

    resetIterator();
    while (next()) {
      Console.print("Bus ");
      Console.print(bus);
      Console.print("; Device ");
      Console.print(device);
      Console.print("; Function ");
      Console.print(function);

      if (isMultiFunctionDevice())
        Console.print("; Multifunction");
      else
        Console.print("; Singlefunction");

      Console.print('\n');

      printCurrentDevice();
    }
  }

  public static void enableBusMasterForCurrentDevice() {
    enableBusMasterFor(bus, device, function);
  }

  public static void enableBusMasterFor(int bus, int device, int function) {
    int register = 1; // register with command in it
    int address = buildAddress(bus, device, function, register);

    MAGIC.wIOs32(addressPort, address);
    int reg2 = MAGIC.rIOs32(dataPort);

    reg2 &= ~(1<<10); // disable bit 10 -> enables interrupts
    reg2 |= 1 << 2; // set bit 2 -> enable bus master
    reg2 |= 1 << 0; // set bit 0 -> enable IO space

    // not sure if this is needed again, but better safe than sorry (some IO ports behave strange...)
    MAGIC.wIOs32(addressPort, address);

    MAGIC.wIOs32(dataPort, reg2);
  }

  public static boolean isMultiFunctionDevice() { return (readReg(3) & 0x00800000) != 0; }

  public static int getVendorId() { return getVendorId(readReg(0)); }
  public static int getDeviceId() { return getDeviceId(readReg(0)); }
  public static int getBaseClassCode() { return getBaseClassCode(readReg(2)); }
  public static int getSubClassCode() { return getSubClassCode(readReg(2)); }
  public static int getHeaderType() { return getHeaderType(readReg(3)); }
  public static int readBar(int barIndex) { return readReg(4 + barIndex) & 0xFFFFFFFC; }

  public static int getVendorId(int reg0) { return reg0 & 0xFFFF; }
  public static int getDeviceId(int reg0) { return (reg0 >>> 16) & 0xFFFF; }
  public static int getBaseClassCode(int reg2) { return (reg2 >>> 24) & 0xFF; }
  public static int getSubClassCode(int reg2) { return (reg2 >>> 16) & 0xFF; }
  public static int getHeaderType(int reg3) { return reg3 >>> 16 & 0xF; }

  private static void printCurrentDevice() {
    int reg0 = readReg(0);
    int reg1 = readReg(1);
    int reg2 = readReg(2);
    // int reg3 = readReg(3);

    int vendorId = getVendorId(reg0);
    int deviceId = getDeviceId(reg0);

    Console.print("VendorId: ");
    Console.printHex((short)vendorId, (byte)7);
    Console.print("    DeviceId: ");
    Console.printHex((short)deviceId, (byte)7);
    Console.print("    Command: ");
    Console.printHex((short)(reg1 & 0xFFFF), (byte)7);
    Console.print('\n');

    int baseClassCode = getBaseClassCode(reg2);
    int subClassCode = getSubClassCode(reg2);

    Console.print("Baseclass: ");
    Console.print(BaseClassCode.convertToString(baseClassCode));
    Console.print(" (");
    Console.printHex((byte)baseClassCode, SymbolColor.DEFAULT);
    Console.print(")");
    Console.print("    Subclass: ");
    Console.printHex((byte)subClassCode, SymbolColor.DEFAULT);

    Console.print("\n\n");
  }

  /*
   * bus: 0-255
   * device: 0-31
   * function: 0-7
   * register: 0-63
   */
  private static int buildAddress(int bus, int device, int function, int register) {
    return (0x80 << 24)
      | (bus << 16)
      | (device << 11)
      | (function << 8)
      | (register << 2);
  }
}