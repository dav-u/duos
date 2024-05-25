package kernel.hardware.ac97;

import kernel.hardware.PCI;

public class AC97 {
  private static int bus, device, function;
  private static boolean foundPciFunction = false;      

  public static boolean setup() {
    PCI.resetIterator();

    while (PCI.next()) {
      if (PCI.getBaseClassCode() == PCI.BaseClassCode.MultimediaDevice
          && PCI.getSubClassCode() == 0x1) {
        bus = PCI.bus;
        device = PCI.device;
        function = PCI.function;
        foundPciFunction = true;
        break;
      }
    }

    if (!foundPciFunction) {
      return false; // could not find AC97
    }

    // AC97 is still set in PCI

    PCI.enableBusMasterForCurrentDevice();

    return true;
  }
}