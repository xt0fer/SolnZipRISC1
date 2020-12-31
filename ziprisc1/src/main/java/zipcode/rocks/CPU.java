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

    // CPU implementation.

    // public static enum R$ {
    //     x0(0), x1(1), x2(2), x3(3), x4(4), x5(5), x6(6), x7(7), x8(8), x9(9), xA(10), xB(11), xC(12), xD(13), xE(14),
    //     xF(15), IR(14), PC(15);

    //     private int numVal;

    //     R$(int numVal) {
    //         this.numVal = numVal;
    //     }

    //     public int getNumVal() {
    //         return numVal;
    //     }
    // }

    private void checkAddress(int address) throws PanicException {
        if (address < 0 || address > MEMORY_LIMIT) {
            System.out.printf("wrong address %04X %d\n", address, address);
            this.dumpState();
            throw new PanicException("address out of range");
        }
    }

    @Override
    public void store(int address, Word w) throws PanicException {
        this.checkAddress(address);
        memory[address] = w;
    }

    @Override
    public Word fetch(int address) throws PanicException {
        this.checkAddress(address);
        return memory[address];
    }

    @Override
    public void wset(int register, Word w) {
        int i = w.getInt();
        if (register == 0xE) {
            this.instruction.set(i);
            System.out.printf("instruction: [%s]\n", this.instruction.toString());            
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
    public int inputInt() throws PanicException {
        // TODO input routine
        try {
            this.inputWord = System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new PanicException("IO Exception fron Java");
        }
        return inputWord;
    }

    @Override
    public void outputInt(int i) {
        // TODO output routine
        System.out.print(outputWord);
    }

    public void dumpState() {
        System.out.println("==== Registers");
        int i = 0;
        for (Integer reg : this.registerFile) {
            System.out.printf("%X - %08X - %d\n", i, reg, reg );
            i++;
        }
        i = 0;
        System.out.println("==== Memory");
        for (Word w : this.memory) {
            if (w.isZero() == false) {
                System.out.printf("0x%04X - %s\n", i, w.toString() );
            }
            i++;            
        }
    }
}
