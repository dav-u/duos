#!/bin/bash
mkdir -p build
cd build
# -T sse3 -> code for pentium4 and later
sjc ../src -o boot && qemu-system-i386 -m 32 -boot a -fda BOOT_FLP.IMG
