#!/bin/bash
mkdir -p build
cd build
sjc ../src -o boot && qemu-system-i386 -m 32 -boot a -fda BOOT_FLP.IMG
