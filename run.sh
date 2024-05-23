#!/bin/bash
mkdir -p build
cd build
# -t sse3 -> code for pentium4 and later
# -t ia32 -> architecture
# -T nsop -> prevent stack frame optimization (methods without local variables should get a stackframe as well)
# sjc ../src -t ia32 -T nsop -o boot && qemu-system-i386 -m 32 -boot a -fda BOOT_FLP.IMG
# sjc ../src -t ia32 -T nsop -o boot && qemu-system-i386 -m 32 -boot a -fda BOOT_FLP.IMG -machine pcspk-audiodev=snd0 -audiodev pa,id=snd0 # -machine pcspk-audiodev=snd0 -audiodev dsound,id=snd0

# sjc ../src -t ia32 -T nsop -o boot -u rte && qemu-system-i386 -m 32 -boot a -fda BOOT_FLP.IMG -device intel-hda -device hda-output
sjc ../src -t ia32 -T nsop -o boot -u rte -g -s 1440k && qemu-system-i386 -m 32 -boot a -fda BOOT_FLP.IMG -device intel-hda -device hda-output -serial file:serial.log