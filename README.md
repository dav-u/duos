# DUOS

DUOS is an operating system implemented in Java.
It serves as a prerequisite for the exam in "Betriebssystem im Eigenbau" by Prof. Dr. Stefan Frenz.

## Usage

### System Shortcuts

- To shut down the system use `Ctrl`+`Shift`+`Alt`+`S`
- To issue a breakpoint interrupt use `Ctrl`+`Alt`+`C`
  - The breakpoint interrupt triggers the bluescreen displaying a stacktrace and the register values
- To stop a long-running task that is not returning use `Ctrl`+`Alt`+`K` to kill it.

## Build and Run

```bash
sjc . -o boot && qemu-system-i386 -m 32 -boot a -fda BOOT_FLP.IMG
```

## Custom Project

Part of the prerequisite is to implement a custom project in the operating system.

This repo implements support for the AC'97 audio codec to play sounds like a synthesizer
via keyboard.

Resources for the project:
- [Audio Codec '97](https://web.archive.org/web/20090824055417/http://download.intel.com/support/motherboards/desktop/sb/ac97_r23.pdf)
- ["1.2.1 AC'97 Compatibility" from Intels _High Definition Audio Specification_](https://www.intel.com/content/dam/www/public/us/en/documents/product-specifications/high-definition-audio-specification.pdf)
- [OSDev AC97](https://wiki.osdev.org/AC97#Detecting_AC97_sound_card)
- [av97.c from BleskOS](https://github.com/VendelinSlezak/BleskOS/blob/master/source/drivers/sound/ac97.c)