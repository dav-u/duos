package kernel.hardware.vesa;

import kernel.Kernel;
import kernel.Memory;
import kernel.bios.BIOS;
import kernel.io.console.Console;

public class VESAGraphics extends VGA {
  public VESAMode modes, curMode;
  
  private final static VESAControllerInfoStruct contrInfo
    = (VESAControllerInfoStruct)MAGIC.cast2Struct(Memory.SCRATCH);
  private final static VESAModeInfoStruct modeInfo
    = (VESAModeInfoStruct)MAGIC.cast2Struct(Memory.SCRATCH);
  
  public static VESAGraphics detectDevice() {
    int modePtr, modeNr;
    VESAGraphics me;
    VESAMode mode;
    
    //get information through real mode interrupt
    contrInfo.id=0x32454256; //VBE2
    BIOS.regs.EAX=0x4F00; //get controller information
  //  BIOS.regs.DS=(short)(KernelConst.KM_SCRATCH>>>4);
    BIOS.regs.ES=(short)(Memory.SCRATCH>>>4); 
    BIOS.regs.EDI=Memory.SCRATCH&0xF;
    BIOS.rint(0x10);

    //check signatures
    if ((short)BIOS.regs.EAX!=(short)0x004F) return null;
    if (contrInfo.id!=0x41534556) return null; //VESA
    
    //VESA detected, get information of controller info struct
    if (contrInfo.version<(byte)2) return null; //at least version 1.2 required
    me=new VESAGraphics();
    modePtr=(((int)contrInfo.videoModePtrSeg&0xFFFF)<<4)+((int)contrInfo.videoModePtrOff&0xFFFF);
    
    //get all available modi
    while ((modeNr=(int)MAGIC.rMem16(modePtr)&0xFFFF)!=0xFFFF) {
      mode=new VESAMode();
      mode.modeNr=modeNr;
      mode.nextMode=me.modes;
      me.modes=mode;
      modePtr+=2;
    }
    
    //get information for available modi (cannot be done above because info-struct is overwritten)
    mode=me.modes;
    while (mode!=null) {
      BIOS.regs.EAX=0x4F01; //get mode information
      BIOS.regs.ECX=mode.modeNr;
      BIOS.regs.ES=(short)(Memory.SCRATCH>>>4);
      BIOS.regs.EDI=Memory.SCRATCH&0xF;
      BIOS.rint(0x10);
      if ((modeInfo.attributes&VESAModeInfoStruct.ATTR_LINFRMBUF)==(short)0) { //no linear frame buffer
        //TODO remove mode from list
        mode.modeNr=-1;
      }
      else { //linear frame buffer supported
        mode.graphical=(modeInfo.attributes&VESAModeInfoStruct.ATTR_GRAPHICAL)!=(short)0;
        mode.xRes=(int)modeInfo.xRes&0xFFFF;
        mode.yRes=(int)modeInfo.yRes&0xFFFF;
        mode.colDepth=(int)modeInfo.colDepth&0xFF;
        mode.lfbAddress=modeInfo.lfbAddress;
      }
      mode=mode.nextMode;
    }
    
    //return driver object
    return me;
  }
  
  public void setTextMode() {
    curMode=null;
    super.setTextMode();
  }
  
  public void setGraphics320x200Mode() {
    curMode=null;
    super.setGraphics320x200Mode();
  }

  public void printModi(){
	  VESAMode mode=modes;

	  while(mode!=null){
		  Console.print("xRES: ");
		  Console.print(mode.xRes);
		  Console.print(" yRES: ");
		  Console.print(mode.yRes);
		  Console.print(" colDepth: ");
		  Console.print(mode.colDepth);
		  Console.print(" Graphical: ");
		  if(mode.graphical) Console.print("true \n");
		  else Console.print("false\n");
		  mode=mode.nextMode;
	  }
  }
  
  public boolean setMode(int xRes, int yRes, int colDepth, boolean graphical) {
    VESAMode mode;
    
    mode=modes;
    while (mode!=null) {
      if (mode.xRes==xRes && mode.yRes==yRes && mode.colDepth==colDepth
          && mode.graphical==graphical) {
        BIOS.regs.EAX=0x4F02; //set current mode
        BIOS.regs.EBX=mode.modeNr;
        BIOS.rint(0x10);
        curMode=mode;
        return true;
      }
      mode=mode.nextMode;
    }
    return false;
  }
  public int getStartAddr(){
	  return curMode.lfbAddress;
  }  
  
  public void setPixel(int x, int y, int col) {
    int addr;
    
    if (curMode==null) {
      super.setPixel(x, y, col);
      return;
    }
    if (x<0 || x>=curMode.xRes || y<0 || y>curMode.yRes) return;
    switch (curMode.colDepth) {
      case 8:
        MAGIC.wMem8(curMode.lfbAddress+x+y*curMode.xRes, (byte)col);
        return;
      case 15: case 16:
        MAGIC.wMem16(curMode.lfbAddress+((x+y*curMode.xRes)<<1), (short)col);
        return;
      case 24:
        addr=curMode.lfbAddress+(x+y*curMode.xRes)*3;
        MAGIC.wMem16(addr, (short)col);
        MAGIC.wMem8(addr+2, (byte)(col>>>16));
   
        return;
      case 32:
        MAGIC.wMem32(curMode.lfbAddress+((x+y*curMode.xRes)<<2), col);
        return;
    }
  }
  
  public void drawLine(int y, int[] col) {
    int src, dst, cnt;
    
    if (curMode==null) {
      super.drawLine(y, col);
      return;
    }
    if (col==null || col.length<curMode.xRes) return;
    MAGIC.inline(0x56);             //push esi
    MAGIC.inline(0x57);             //push edi
    MAGIC.inline(0xFC);             //cld
    src=(int)MAGIC.addr(col[0]);
    MAGIC.ignore(src);
    cnt=curMode.xRes;
    MAGIC.ignore(cnt);
    switch (curMode.colDepth) {
      case 8:
        dst=curMode.lfbAddress+(y*curMode.xRes<<1);
        MAGIC.ignore(dst);
        MAGIC.inline(0x8B, 0x75); MAGIC.inlineOffset(1, src); //mov esi,[e/rbp-4/8]
        MAGIC.inline(0x8B, 0x7D); MAGIC.inlineOffset(1, dst); //mov edi,[e/rbp-8/16]
        MAGIC.inline(0x8B, 0x4D); MAGIC.inlineOffset(1, cnt); //mov ecx,[e/rbp-12/24]
        MAGIC.inline(0xAD);          //nextPixel: lodsd
        MAGIC.inline(0xAA);             //stosb
        MAGIC.inline(0xE2, 0xFC);       //loop nextPixel
        break;
      case 15: case 16:
        dst=curMode.lfbAddress+(y*curMode.xRes<<1);
        MAGIC.ignore(dst);
        MAGIC.inline(0x8B, 0x75); MAGIC.inlineOffset(1, src); //mov esi,[e/rbp-4/8]
        MAGIC.inline(0x8B, 0x7D); MAGIC.inlineOffset(1, dst); //mov edi,[e/rbp-8/16]
        MAGIC.inline(0x8B, 0x4D); MAGIC.inlineOffset(1, cnt); //mov ecx,[e/rbp-12/24]
        MAGIC.inline(0xAD);          //nextPixel: lodsd
        MAGIC.inline(0x66, 0xAB);       //stosw
        MAGIC.inline(0xE2, 0xFB);       //loop nextPixel
        break;
      case 24:
        dst=curMode.lfbAddress+y*curMode.xRes*3;
        MAGIC.ignore(dst);
        MAGIC.inline(0x8B, 0x75); MAGIC.inlineOffset(1, src); //mov esi,[e/rbp-4/8]
        MAGIC.inline(0x8B, 0x7D); MAGIC.inlineOffset(1, dst); //mov edi,[e/rbp-8/16]
        MAGIC.inline(0x8B, 0x4D); MAGIC.inlineOffset(1, cnt); //mov ecx,[e/rbp-12/24]
        MAGIC.inline(0x66, 0xA5);    //nextPixel: movsw
        MAGIC.inline(0x66, 0xAD);       //lodsw
        MAGIC.inline(0xAA);             //stosb
        MAGIC.inline(0xE2, 0xF9);       //loop nextPixel
        break;
      case 32:
        dst=curMode.lfbAddress+(y*curMode.xRes<<2);
        MAGIC.ignore(dst);
        MAGIC.inline(0x8B, 0x75); MAGIC.inlineOffset(1, src); //mov esi,[e/rbp-4/8]
        MAGIC.inline(0x8B, 0x7D); MAGIC.inlineOffset(1, dst); //mov edi,[e/rbp-8/16]
        MAGIC.inline(0x8B, 0x4D); MAGIC.inlineOffset(1, cnt); //mov ecx,[e/rbp-12/24]
        MAGIC.inline(0xF3, 0xA5);    //nextPixel: rep movsd
        break;
    }
    MAGIC.inline(0x5F);             //pop e/rdi
    MAGIC.inline(0x5E);             //pop e/rsi
  }
}
