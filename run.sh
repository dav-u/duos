#!/bin/bash
sjc . -o boot && qemu-system-i386 -m 32 -boot a -fda BOOT_FLP.IMG
