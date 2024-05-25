package rte;
public class SPackage { //zusätzliche Klasse
  public static SPackage root; //statisches Feld für root-Package
  public String name; //einfacher Name des Packages
  public SPackage outer; //höhergelegenes Package, noPackOuter deaktiviert*
  public SPackage subPacks; //erstes tiefergelegenes Package
  public SPackage nextPack; //nächstes Package auf gleicher Höhe
  public SClassDesc units; //erste Unit des aktuellen Packages

  public String getMethodNameFromIp(int instructionPointer) {
    // search units
    SClassDesc unit = this.units;
    while (unit != null) {
      String name = unit.getMethodNameFromIp(instructionPointer);
      if (name != null) return name; // maybe append unit name

      unit = unit.nextUnit;
    }

    // search sub packages
    SPackage subPackage = this.subPacks;

    while (subPackage != null) {
      String name = subPackage.getMethodNameFromIp(instructionPointer);
      if (name != null) return name; // maybe append pkg name

      subPackage = subPackage.nextPack;
    }

    return null;
  }
}