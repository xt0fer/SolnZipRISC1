# ZAS - ZipRISC1 Assembler

`zas` takes a `.zas` file and translates it into ZipRISC1 machine code.

Because the layout of the instructions of the processor are always 32-bit, it makes the code generation pretty easy. Each line in the output file represents one "word" (4 bytes, 32bits) of main memory in the processor.
Each line number is therefore the main memory address. In the output file, it is shown as a 4-digit hexadecimal number. The assembler keeps track of this in the ZAS instance variable `address` which is incremented after each word is added to the instruction list.

The basic idea is that it reads in each line of the input file and attempts to sort the line into one of three patterns: 

- label line
- directive line
- instruction line

When it decides, using regex patterns, which of the three line patterns it is, it dispatches the line into one of three handling routines.

The label line is easy. It takes the current global address tracking variable and inserts the symbols name into the `symbols` table, associating a String with a memory address (the current global address).

The directive line implements one of the few assembler directives we use.

The instruction (code) handler breaks the line into tokens and then based on the opcode (tokens[0]), produces a given `WordAt` object. This is then appended to the List kept of each Instruction.

At the end of the file, any forward symbol references are resolved, as the output is being turned into text.

`zas` uses System.err as its debugging and diagnostic output, System.out for the machine code production.

The machine code file is utf-8 text, not a binary data file, which makes it easier to look at and debug.