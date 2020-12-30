# ZipRISC1

v1.0

The ZipCode RISC-1 microprocessor needs a simulator to prove to the investors that this is a world-beating design an Intel/Amd and apple will all shake in their shoes when they see how fast and clean and cool this processor is.

You need to write a processor simulator. It reads in a file of machine code, loads it into memory and starts execution. The simulation continues until either a CRASH or a completion of the program.

- registers: 16 32-bit named x0 to xF (PC is xF, IC is xE) (x0 is ALWAYS zero)
- memory: 0x0000 - 0xFFFF (16K words!! (or 64Kbytes))
- I/O: input/output (special registers)
- instruction: 4 bytes, 0, 1, 2, 3
  - opcode, operand1, operand2, operand3

## Instructions

The first column is the “assembly code”, 2nd is the memory layout, third is “meaning”

### Core Instructions

ADD rd, rs, rt | 1dst | rd <- rs + rt

MOV rd, rs, 0 | 1ds0 | rd <- rs

SUB rd, rs, rt | 2dst | rd <- rs - rt

SUBI rd, rs, k | 3dsk | rd ← rs - k

LSH rd, rs, k | 4dsk | rd <- rs / k ??

RSH rd, rs, k | 5dsk | rd <- rs * k ??

BRZ rd, aa | 6daa | branch to aa on rd == 0

BGT rd, aa | 7daa | branch to aa on rd > 0

LD rd, aa | 8daa | load rd with value of memory loc aa

ST rs, aa | 9saa | store rd value to memory loc aa

HLT | 0000 | halt.

HCF | 0FFF | halt and catch fire.

### Pseudo Instructions

MOV rd, rs │ ADD rd, rs, x0 │ rd ← rs
CLR rd │ ADD rd, x0, x0 │ rd ← 0
DEC rd │ SUBI rd, rd, 1 │ rd ← rd - 1
INCR rd |ADD rd, rd, 1  | rd <- rd + 1
BRA aa │ BRZ x0, aa │ next instruction to read is at aa
IN rd | Ad00 | read in a number to rd
OUT rd | Bd00 | output a number from rd
DUMP | F000 | print out registers, machine state and memory

### Directives

Directives included

.EQ equate for defining decimal constants

```
.EQ Zero 0
.EQ OneHundred 100
```

.OR set origin address of code (load code starting at this address (hex)

```
.OR 0x0000
```

.HS hex string of bytes

```
.HS '001234AFDCE'
```

.AS ascii string of bytes - any delimiter except white space - whatever started it ended it

```
.AS ‘This is a string.’
```

.TF target file for output

```
.TF filename.zex
```

.BS n reserve block storage of n (decimal) bytes

```
.BS 64
```

### Sample Programs

#### Example program

loop:LD x1, 90LD x2, 90ADD x1, x2ST x1, 91BRA loop

doubles value at aa 90

loop:LD x1, 90ADD x1, x1OUT x1BRA loop

multiply by 8, value at aa 90

loop:LD x1, 90ADD x1, x1ADD x1, x1ADD x1, x1ST x1, 91BRA loop

Max function (read two inputs, output the larger of the two)

loop:LD x1, 90LD x2, 90SUB x1, x1, x2 // x1 <- x1 - x2BGT x1, first // if x1 > 0 goto first:second:ST x2, 91BRA loopfirst:OUT x1BRA loop

Build me a Guess the Number program?

## Toolchain

### Assembler

Build an assembler/compiler.

translates .zas file to .zex file

A ZAS file is a text file which contain the lines of a program which runs on the ZipRISC1

File format is simple: a line is either empty, a directive, label, or an instruction

On any line, anything after a `//` is a comment. For readability, directives and labels should start at `^` and Instruction lines should start with `^\t`

```
start:
    HLT // stop the machine. exit status is zero.
```

### Loader/Evaluator

Build a microcode evaluator.

load a .zas file into memory and start execution at 0x0000.
a common thing to do is to load 0x0000 with a `BRA 0x0090` (06 00 00 90)

```
load PC with 0
    FETCH IR (LD xE 0x0000)
    INCR PC
    EXECUTE IR
```

Main Processor Data Structures
- status (1 word)(flags??)
- registers (16 words)
- memory (16384 words)
- input word
- output word

