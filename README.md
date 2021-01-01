# ZipRISC1

v1.2

This lab/project can be done in either Java, Javascript, Python or any other language you manage. 
Just ask permission if you want to do it in something other than Java or Python.

The ZipCode RISC-1 (a 32-bit) microprocessor needs a simulator to prove to the investors that this is a world-beating design that Intel, AMD and Apple will all shake in their shoes when they see how fast and clean and cool this processor is.

It has a simple internal core architecture, and a simple set of instructions. 
It is "turing-complete". This processor is a little (very little) like the new Apple Silicon M1s, in that it has memory side-by-each with the registers (as in the memory is inside of the CPU)
.
Its memory is not a separate subsystem.

You need to write a processor simulator. It reads in a file of machine code (.zex file), loads it into memory and starts execution. The simulation continues until either a CRASH or a completion of the program.
When you start the program, you execute the instruction found at memory location 0x0000. 

Each memory location is a 32-bit "word" made up of 4 "bytes". Each byte can only contain numbers from 0-255.

There are 16 registers, numbered 0 to 15 (or x0 to xF). Register 15 (F) is used as the Program Counter (PC). It contains the address of the next instruction to be executed. Register 14 is the Instruction Register. It is the place where the current instruction is placed just before it is executed. The processor runs a program from 0x0000 until it told to halt (HLT). When it is told to HALT, and no errors have occurred, you can consider your program to have "run".

So this lab/project has you implementing code for our ZipRISC1 processor. We have provided a simple version of the main loop of the simulator which onlt implements HLT. You must implement the rest of the instructions.

We have also provide a very stupid, simple "assembler" which can translate ZipRISC1 Assembly (.zas file) code file (UTF-8 text) (and human readable-ish) into the ZipRISC1 executable format (.zex) (which is a UTF-* text file the simulator's loader can load into the the processor's memory.)

The assembly file is a program file which tries to do some kind of simple task. 
Each line is one of four possible layouts, and if you mess up the layout, well, you get a very simple error message. The assembler quits as soon as it finds an error, or runsuntil the input runs out, and then drops the output file. You then start the simulator on the output of the assembler and see what happens. You may get some output, an error, or maybe even a Panic. Panics are bad. Panics mean someting is very wrong with something you're trying to do.

### To RECAP

- registers: 16 32-bit named x0 to xF (PC is xF, IR is xE) (x0 is ALWAYS zero)
  - numbered 0 to 15
- memory: 0x0000 - 0xFFFF (16K words!! (or 64Kbytes))
- I/O: input/output (special registers)
- instruction: 4 bytes, 0, 1, 2, 3
  - opcode, operand1, operand2, operand3

## ZipRISC1 Instructions

The first column is the “assembly code”, 2nd is the memory layout of the instruction (4 bytes), third is “meaning” in psuedo-code.

### Core Instructions

- ADD rd, rs, rt | 1dst | rd <- rs + rt
- MOV rd, rs, 0 | 1ds0 | rd <- rs
- SUB rd, rs, rt | 2dst | rd <- rs - rt
- SUBI rd, rs, k | 3dsk | rd ← rs - k
- LSH rd, rs, k | 4dsk | rd <- rs / k ??
- RSH rd, rs, k | 5dsk | rd <- rs * k ??
- BRZ rd, aa | 6daa | branch to aa on rd == 0
- BGT rd, aa | 7daa | branch to aa on rd > 0
- LD rd, aa | 8daa | load rd with value of memory loc aa
- ST rs, aa | 9saa | store rd value to memory loc aa
- HLT | 0000 | halt.
- HCF | 0FFF | halt and catch fire.

### Pseudo Instructions

These are just handy, the text in the first column gets translated to the instruction in the second column.

- MOV rd, rs │ ADD rd, rs, x0 │ rd ← rs
- CLR rd │ ADD rd, x0, x0 │ rd ← 0
- DEC rd │ SUBI rd, rd, 1 │ rd ← rd - 1
- INCR rd |ADD rd, rd, 1  | rd <- rd + 1
- BRA aa │ BRZ x0, aa │ next instruction to read is at aa
- IN rd | Ad00 | read in a number to rd
- OUT rd | Bd00 | output a number from rd
- DUMP | F000 | print out registers, machine state and memory

### Assembler Directives

Directives included help layout code in the memory. They are kind like macros.

.OR set origin address of code (load code starting at this address (hex)

```
.OR 0x0000
```

.WD load next memory word with decimal number

```
.WD 2
```

.EQ equate for defining decimal constants

```
.EQ Zero 0
.EQ OneHundred 100
```
(but I am not sure we need this quite yet. nor whether the assembler will resolve the symbol correctly)

### not yet implemented 

.HS hex string of bytes

```
.HS '001234AFDCE'
```

.AS ascii string of bytes - any delimiter except white space - whatever started it ended it

```
.AS ‘This is a string.’
```

.BS n reserve block storage of n (decimal) bytes

```
.BS 64
```

.EQ equate for defining decimal constants

```
.EQ Zero 0
.EQ OneHundred 100
```


### Sample Programs

#### Add 2 plus 2

```
.OR 0x0000 // start at address zero
    BRA start
two:
.WD 2
start:
    LD x1, two
    LD x2, two
    ADD x1, x1, x2
    DUMP
    HLT
```
The output from zas of this file would be a UTF-8 file of hex numbers.

```
0x0000 06 00 00 02 // BRA start
0x0001 00 00 00 02 // two = 2 (two is 0x0001)
0x0002 08 01 00 02 // LD x1, two
0x0003 08 02 00 02 // LD x2, two
0x0004 01 01 01 02 // ADD
0x0005 0C 00 00 00 // dump
0x0006 00 00 00 00 // HLT
```


#### Get two numbers and print their sum.

```
loop:
    IN x1
    IN x2
    ADD x1, x2
    OUT x1
    BRA loop
```


read in a number and double it

```
loop:
    IN x1
    ADD x1, x1
    OUT x1
    BRA loop
```

multiply by 8, value at aa 90

```
loop:
    IN x1
    ADD x1, x1
    ADD x1, x1
    ADD x1, x1
    OUT x1
    BRA loop
```

Max function (read two inputs, output the larger of the two)

```
loop:
    IN x1
    IN x2
    SUB x1, x1, x2 // x1 <- x1 - x2
    BGT x1, first // if x1 > 0 goto first:
second:
    ST x2, 91
    BRA loop
first:
    OUT x1
    BRA loop
```

Build me a Guess the Number program?

## Toolchain

### ZAS Assembler

`zas` translates .zas file to standard out (which you should put into a .zex file).

A ZAS assembler file is a text file which contain the lines of a program which runs on the ZipRISC1.

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

You get to graduate from ZipCode early if you write a C compiler for this processor. Several corporate partners may actually compete to hire you for bigger than normal money if you manage that.