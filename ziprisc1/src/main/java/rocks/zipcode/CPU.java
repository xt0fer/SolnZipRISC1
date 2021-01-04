package rocks.zipcode;

import java.io.IOException;
import java.lang.System;

public class CPU implements RISC1Core {

    // convenience constants for the last two registers
    final static int MAXREGS = 16;
    final static int PC = MAXREGS-1; // Program Counter
    final static int IR = MAXREGS-2; // Instruction Decode Register
    final static int SP = MAXREGS-3; // Stack Pointer
    final static int FP = MAXREGS-4; // Frame Pointer

    private Integer[] registerFile = new Integer[MAXREGS];

    final static int MEMORY_SIZE = 0x100; 
    private Word[] memory = new Word[MEMORY_SIZE];
    private Word instruction = new Word(0);

    private int inputWord = 0;
    private int outputWord = 0;
    int statusWord = 0;

    public CPU() {
        this(0);
    }

    // initial all registers to a specific value
    // zero out memory
    public CPU(int rinit) {
        for (int i=0; i < MAXREGS; i++) {
            registerFile[i] = rinit;
        }
        for (int i=0; i < MEMORY_SIZE; i++) {
            memory[i] = new Word(0);
        }

    }

    public int memorysize() {
        return MEMORY_SIZE;
    }
    public int registerfilesize() {
        return MAXREGS;
    }
    // CPU state manipulation
    // primarily here for Engine's methods.
    public boolean isRunnable() {
        return (this.statusWord == 0);
    }
    public boolean setRunnable() {
        this.statusWord = 0;
        return true;
    }
    public boolean haltCPU() {
        this.statusWord = -1;
        return true;
    }

    private void checkAddress(int address)  {
        if (address < 0 || address > MEMORY_SIZE) {
            System.err.printf("wrong address %04X %d\n", address, address);
            this.dumpState();
            throw new Panic("address out of range");
        }
    }

    // store a Word at memory address
    @Override
    public void store(int address, Word w)  {
        this.checkAddress(address);
        memory[address] = w;
    }

    // fetch a Word from memory address
    @Override
    public Word fetch(int address)  {
        this.checkAddress(address);
        return memory[address];
    }

    // set register to contents of Word
    @Override
    public void wset(int register, Word w) {
        int i = w.getInt();
        if (register == 0xE) {
            instruction.set(i);
            System.err.printf("instruction: [%s]\n", instruction.toString());
        }
        this.set(register, i);
    }

    // convenience methods for Instruction decode.

    public int opcode() {
        return instruction.opcode();
    }

    public int arg1() {
        return instruction.arg1();
    }
    
    public int arg2() {
        return instruction.arg2();
    }
    
    public int arg3() {
        return instruction.arg3();
    }
    
    // get a Word from register
    @Override
    public Word wget(int register) {
        // TODO should this not create a new Word?
        return new Word(get(register));
    }

    // get int from register
    @Override
    public int get(int register) {
        if (register == 0) return 0; //always return zero from Reg 0.
        return this.registerFile[register];
    }

    // set register to int
    @Override
    public void set(int register, int i) {
        if (register == 0) return; // never allow reg 0 to be set.
        this.registerFile[register] = i;
    }

    @Override
    public void halt() {
        // just stop already.
        this.haltCPU();
    }

    // IO routines

    @Override
    public int inputInt()  {
        // TODO input routine
        // mkae this real.
        try {
            this.inputWord = System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Panic("input failed.");
        }
        return inputWord;
    }

    @Override
    public void outputInt(int i) {
        // TODO output routine
        System.err.print(outputWord);
    }

    public void dumpState() {
        // if the register is set to zero, do not DUMP
        System.err.println("==== Registers");
        int i = 0;
        for (Integer reg : this.registerFile) {
            if (reg != 0) {
                System.err.printf("%X - %08X - %d\n", i, reg, reg );
            }
            i++;
        }

        // if the contents of memory Word is set to zero, do not DUMP
        i = 0;
        System.err.println("==== Memory");
        for (Word w : memory) {
            if (w.isZero() == false) {
                System.err.printf("0x%04X - %s\n", i, w.toString() );
            }
            i++;
        }
    }
}
