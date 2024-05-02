package kernel.hardware;

import kernel.io.console.Console;
import kernel.io.console.SymbolColor;

public class PCI {
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

  public static void printDevices() {
    Console.print("Devices:\n");

    int busSize = 3;
    int deviceSize = 32;
    int functionSize = 8;
    for (int bus = 0; bus < busSize; bus++) {
      for (int device = 0; device < deviceSize; device++) {
        for (int function = 0; function < functionSize; function++) {
          MAGIC.wIOs32(addressPort, buildAddress(bus, device, function, 0));
          int reg0 = MAGIC.rIOs32(dataPort);
          MAGIC.wIOs32(addressPort, buildAddress(bus, device, function, 1));
          int reg1 = MAGIC.rIOs32(dataPort);
          MAGIC.wIOs32(addressPort, buildAddress(bus, device, function, 2));
          int reg2 = MAGIC.rIOs32(dataPort);
          MAGIC.wIOs32(addressPort, buildAddress(bus, device, function, 3));
          int reg3 = MAGIC.rIOs32(dataPort);

          if (reg0 == 0 || reg0 == -1)
            continue;

          Console.print("Bus ");
          Console.print(bus, SymbolColor.DEFAULT);
          Console.print("; Device ");
          Console.print(device, SymbolColor.DEFAULT);
          Console.print("; Function ");
          Console.print(function, SymbolColor.DEFAULT);
          boolean isMultiFunctionDevice = (reg3 & 0x00800000) != 0;

          if (isMultiFunctionDevice) {
            Console.print("; Multifunction");
          }
          else {
           Console.print("; Singlefunction");
           function = 8;
          }
          Console.print('\n');

          printDevice(reg0, reg1, reg2, reg3);
        }
      }
    }
  }

  private static void printDevice(int reg0, int reg1, int reg2, int reg3) {
    int vendorId = reg0 & 0xFFFF;
    int deviceId = (reg0 >>> 16) & 0xFFFF;

    Console.print("VendorId: ");
    Console.printHex((short)vendorId, (byte)7);
    Console.print("    DeviceId: ");
    Console.printHex((short)deviceId, (byte)7);
    Console.print('\n');

    int baseClassCode = (reg2 >>> 24) & 0xFF;
    int subClassCode = (reg2 >>> 16) & 0xFF;

    Console.print("Baseclass: ");
    Console.print(BaseClassCode.convertToString(baseClassCode));
    Console.print(" (");
    Console.printHex((byte)baseClassCode, SymbolColor.DEFAULT);
    Console.print(")");
    Console.print("    Subclass: ");
    Console.print(BaseClassCode.convertToString(subClassCode)); // are subclasscodes the same as baseclasscodes?
    Console.print(" (");
    Console.printHex((byte)subClassCode, SymbolColor.DEFAULT);
    Console.print(")");

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