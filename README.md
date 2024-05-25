# DUOS

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
