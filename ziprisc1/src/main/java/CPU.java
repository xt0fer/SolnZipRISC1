import java.io.IOException;
import java.lang.System;

public class CPU implements RISC1Core {

    final static int PC = 0xF;
    final static int IR = 0xE;

    private Integer[] registerFile = new Integer[16];

    final static int MEMORY_LIMIT = 0x100;
    private Word[] memory = new Word[MEMORY_LIMIT];
    private Word instruction = new Word(0);

    private int inputWord = 0;
    private int outputWord = 0;
    int statusWord = 0;

    public CPU() {
        this(0);
    }

    public CPU(int rinit) {
        for (int i=0; i < 16; i++) {
            registerFile[i] = rinit;
        }
        for (int i=0; i < MEMORY_LIMIT; i++) {
            memory[i] = new Word(0);
        }

    }

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
        if (address < 0 || address > MEMORY_LIMIT) {
            System.err.printf("wrong address %04X %d\n", address, address);
            this.dumpState();
            throw new Panic("address out of range");
        }
    }

    @Override
    public void store(int address, Word w)  {
        this.checkAddress(address);
        memory[address] = w;
    }

    @Override
    public Word fetch(int address)  {
        this.checkAddress(address);
        return memory[address];
    }

    @Override
    public void wset(int register, Word w) {
        int i = w.getInt();
        if (register == 0xE) {
            this.instruction.set(i);
            System.err.printf("instruction: [%s]\n", this.instruction.toString());            
        }
        this.set(register, i);
    }

    public int opcode() {
        return this.instruction.opcode();
    }

    public int arg1() {
        return this.instruction.arg1();
    }
    
    public int arg2() {
        return this.instruction.arg2();
    }
    
    public int arg3() {
        return this.instruction.arg3();
    }
    
    @Override
    public Word wget(int register) {
        return new Word(this.get(register));
    }

    @Override
    public int get(int r) {
        if (r == 0)
            return 0;
        return this.registerFile[r];
    }

    @Override
    public void set(int r, int i) {
        if (r == 0)
            return;
        this.registerFile[r] = i;
    }

    @Override
    public void halt() {
        // just stop already.
    }

    @Override
    public int inputInt()  {
        // TODO input routine
        try {
            this.inputWord = System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new Panic("IO Exception fron Java");
        }
        return inputWord;
    }

    @Override
    public void outputInt(int i) {
        // TODO output routine
        System.err.print(outputWord);
    }

    public void dumpState() {
        System.err.println("==== Registers");
        int i = 0;
        for (Integer reg : this.registerFile) {
            System.err.printf("%X - %08X - %d\n", i, reg, reg );
            i++;
        }
        i = 0;
        System.err.println("==== Memory");
        for (Word w : this.memory) {
            if (w.isZero() == false) {
                System.err.printf("0x%04X - %s\n", i, w.toString() );
            }
            i++;            
        }
    }
}
