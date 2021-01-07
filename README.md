# ZipRISC1

## Changes Immediate

- re-arrange instructions to re-order opcode(labels & hexs)
- re-arrange the register assignments??
- add STR, LDR (or Load Indirect and Store Indirect?) (push & pop)
- ADDI re-order
- make one source of truth for instructions (json??)

## v1.2

This lab/project can be done in either Java, Javascript, Python or any other language you manage. 
Just ask permission if you want to do it in something other than Java or Python.

The ZipCode RISC-1 (a 32-bit) microprocessor needs a simulator to prove to the investors that this is a world-beating design that Intel, AMD and Apple will all shake in their shoes when they see how fast and clean and cool this processor is.

A CPU has what's called an Instruction Set Architecture (ISA). There are many copyrighted/proprietary ones, like ARM(Apple, Raspberry Pi/Broadcom) and x86_64(Intel/AMD). There are several "open source" ones as well like RISC-V. There are also many, many "demonstration" or "made up" ones. This is one of those.

An ISA is a set of crafted "instructions" which are matched to a CPU design. A common design is a RISC (reduced instruction set computer) where the number of instructions is relatively low number, like say 20, which are all pretty 'regular' or uniform (4 bytes, 32bits) in size. A CISC (complex instruction set computer), on the other hand, has two major aspects that RISC does not. CISC cpus often have hundreds of instructions (x86_64 has more than 1500) and the instructions sizes vary (from 16-bit (2 bytes) to larger (up to 15 bytes)).

Now, why look at ZipRISC? Especially if it's a virtual processor? Well, ZipRISC1 is a pretty simple microprocessor. It also has a simple set of instructions. And while only having a few instructions, it can do anything a CISC cpu can do (theoretically). And because it looks like RISC is winning the long war against CISC.

It has a simple internal core architecture, and a simple set of instructions. 
It is "turing-complete". 
This processor is a little (very little) like the new Apple Silicon M1s, in that it has memory side-by-each with the registers (as in the memory is inside of the CPU).
Its memory is not a separate subsystem (like on an IBM PC architecture machine (which most PCs are)).

The 'von neumann architecture' (look it up) is the idea of a "stored program computer'. (Is that all?) Yes, but it accounts for the vast majority of computer architectures. 
A computer runs a program that has been placed in its central memory, and then it starts at some location, interpreting each memory word as an instruction, each instruction having the cpu do something very simple; like say add two numbers. 
The cpu then goes to the next word in memory, over and over, until it is told to halt.

So, a computer boils down to a CPU and some memory, and a few ancillary functions like input and output.

Dealing with computer instructions can be hard, if you do it in binary.
So we don't.
We do it in this case in hexadecimal.
We also don't want to do it entirely low-level, so we invent languages where we can express our programs in a "higher level way", and then build translator programs which translate the higher level program into machine instructions.

Historically, the lowest level was a "symbolic assembler", which assembled "assembly code" into instructions. That's where this lab is focused.

You write programs in assembly language (The ZAS language), specifically for the ZipRISC1, and turn it into machine code, which you then simulate on your simulator. 

You need to write a processor simulator. It reads in a file of ZipRISC1 machine code (.zex file), loads it into memory and starts execution. 
The simulation continues until either a 'Panic' (crash) or a completion of the program.
When you start the program, you execute the instruction found at memory location 0x0000.

Each memory location is a 32-bit "word" made up of 4 "bytes". Each byte can only contain numbers from 0-255. Memory sizes can be modified as needed. Let's start with 64K words (or 256K bytes).

### ZipRISC1-16/32(256K)

There are 16 registers, numbered 0 to 15 (or x0 to xF). Registers are super-fast places inside a cpu which are used to perform specific instructions. You can

- perform arithmetic on a register
- move 32-bit words from one register to another
- perform simple input and output from/to a register
- move a word in memory to/from a register

Register 15 (F) is used as the Program Counter (PC). It contains the address of the next instruction to be executed. Register 14 is the Instruction Register. It is the place where the current instruction is placed just before it is executed. Register 0 is 'hardwired' to zero (which proves to be surprisingly handy). There are also registers assigned to the stack pointer, the frame pointer, the return address, and to the parameters of a function.

The processor runs a program from 0x0000 until it told to halt (HLT). When it is told to HALT, and no errors have occurred, you can consider your program to have "run".

### ZipRISC1-32/32(4M)

This processor version has 32 registers, it is still a 32-bit cpu, but has 4 megabytes of memory. It's important that you write your cpu simulator so that the same code works for both versions of the processor.
Currently, in version 1.2 of ths lab, this second version of the processor is not yet completely defined.

Neither cpu currently has the notion of "floating point"; that is left as an exercise for the student. 

So this lab/project has you implementing code for our ZipRISC1 processor. We have provided a simple version of the main loop of the simulator which only implements HLT. You must implement the rest of the instructions.

We have also provide a very stupid, simple "assembler" which can translate ZipRISC1 Assembly (.zas file) code file (UTF-8 text) (and human readable-ish) into the ZipRISC1 executable format (.zex) (which is a UTF-* text file the simulator's loader can load into the the processor's memory.)

The assembly file is a program file which tries to do some kind of simple task.
Each line is one of four possible layouts, and if you mess up the layout, well, you get a very simple error message. The assembler quits as soon as it finds an error, or runs until the input runs out, and then drops the output file.
You then start the simulator on the output of the assembler and see what happens.
You may get some output, an error, or maybe even a Panic.
Panics are bad.
Panics mean something is very wrong with something you're trying to do.

### To RECAP

- 16 registers: 32-bits wide named x0 to xF  (x0 is ALWAYS zero)
  - numbered 0 to 15
  - x1 is used for RA
  - xF is PC
  - xE is IR
  - x8, x9, xA 
- memory: 0x0000 - 0xFFFF (16K words!! (or 64Kbytes))
- I/O: input/output (special registers)
- instruction: 4 bytes, numbered 0, 1, 2, 3
  - opcode, operand1, operand2, operand3
- some instruction take one or two operands, some take three.

## ZipRISC1 Instructions

The first column is the “assembly code”, 2nd is the memory layout of the instruction (4 bytes), third is “meaning” in psuedo-code.

### Core Instructions

- rd is the destination register
- rs, rt are "argument" registers
- k stands for an integer constant
- aa stands for an memory address, usually in hexadecimal
- yes, rd, rt, & rs can be all the same register (or not)

- ADD rd, rs, rt | 1dst | rd <- rs + rt
- MOV rd, rs, x0 | 1ds0 | rd <- rs
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
- IN rd | Ad00 | read in a number to rd
- OUT rd | Bd00 | output a number from rd
- ADDI rd rs k | Cdsk | rd <- rs + k  /*yes, added after first design */
- DUMP | F000 | print out registers, machine state and memory

### Pseudo Instructions

These are just handy, the text in the first column gets translated to the instruction in the second column.

- MOV rd, rs │ ADD rd, rs, x0 │ rd ← rs
- CLR rd │ ADD rd, x0, x0 │ rd ← 0 + 0
- DEC rd │ SUBI rd, rd, 1 │ rd ← rd - 1
- INCR rd |ADDI rd, rd, 1  | rd <- rd + 1
- BRA aa │ BRZ x0, aa │ branch to aa, when register zero equals 0
  - (yes x0 is ALWAYS 0) (so think GOTO aa)

* the calling convention for subroutines/functions.
    - CALL aa | ADDI x1 xPC 1; BRA aa | ra <- PC + 1, jump to aa
    - RET | ADD xPC x1 x0 | pc <- ra (ra is "return address")

### Assembler Directives

Directives included help layout code in the memory. They are kind of like macros.

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

### Labels

labels are like a text anchor or target in assembler source code. 
When labels are used they do one of two things (currently).
The first is that they come to represent the address of the next instruction.

```
    LD x2, 0x00FF // memory location 255 
label1:
    BRZ x2, end
// bunch of code
end:
    HLT
```

So when the assembler is finished, `label1` and `end` will both be resolved to a specific address, but we don't know what it will be be because we don't how much code is between the two labels.
The assembler will figure it all out and create code so that the labels point to a valid memory address.
We also see the second use of labels on the `BRZ` line: if register x2 is zero, jump to `end`
So labels can be used to delay figuring out what the address of the HLT instruction is, until we've assembled the entire program.

Labels can also be used like junior variables.

```
// bunch of code
maxspeed:
.WD 55
fueltanklevel:
.WD 2  // 0-4 (empty, 1/4, 2/4 3/4, 4/4)
// more code
```

Here we are using the label to stand in for a variable's name.
The assembler will load the number into a memory word.
Once all the code has been assembled, the label will come to point to the address of the WD directive.


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
    BRZ x1, exit
    IN x2
    ADD x1, x2
    OUT x1
    BRA loop
exit:
    HALT
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

The a microcode evaluator has several parts: 

- CPU class - a very simple POJO that handles memory and registers and cpu status.
- Engine class - where all the processor implementation code goes
- Simulator class - which executes the simulation
- Word class - the model for the memory locations.
  - note that the memory is only word-based (32bit quantities), NOT byte addressable

load a .zas file into memory and start execution at 0x0000.
A common thing to do is to load 0x0000 with a `BRA aa` which allows for data to be loaded into low memory locations. `aa` would be a label like "start" or "main" where the actual code is.

The Simulator then essentially does this: (remember, PS is the Program Counter, the IR is the instruction register)

```
cpuRunning = true
PC = 0
while (cpuRunning == true)
    load IR with contents of Memory[PC]
    PC += 1
    Decode and Execute instruction in IR
    // the HLT instruction sets cpuRunning = false

// dump the cpu and memory final state
```

So, finish up all the instructions, and write some tests to test your instructions.

Then, think about these ideas...

### Ideas to ponder

- how would you multiply two integers?
- How would you make an If statement?
- How about If/Else?
- And loops?
  - while loop
  - For loop
- How about a simple function performing an arithmetic equation?
  - x = x + y - z
  - x = 2 * x - y + z
- How would you implement a simple string?
- What needs to change for string I/O?
- Should the I/O pattern be changed to ONLY UTF-8 in an out?
- How would you parse an integer from a string of bytes in zas?


### Stuff in side of CPU

Main Processor Data Structures

- status (1 word)(flags??)
- registers (16 words)
- memory (16384 words)
- input word
- output word

You get to graduate from ZipCode early if you write a C compiler for this processor.
Several corporate partners may actually compete to hire you for bigger than normal money if you manage that.

### Futures to be Added

- add AND, OR, and XOR instructions
- add PUSH and POP? to the stack at SP
- (maybe even PUSHI and POPI? immediate versions of Push/Pop
- strings - handle unicode arrays in memory

- implement a femtoC compiler
- implement a femtoLisp interpreter
- implement a Forth

_Need a BUNCH of tests!_