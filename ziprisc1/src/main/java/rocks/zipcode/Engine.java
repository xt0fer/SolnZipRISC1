package rocks.zipcode;

/* this Engine should be the 
 * "microcode" runner, the object that 
 * does all the actual work 
 * of the processor */

public class Engine {

    private CPU cpu = null;

	public Engine(CPU cpu) {
        this.cpu = cpu;
    }
    
    // opcodes
    // - HLT | 0000 | halt.
    public final static int HLT = 0x00;
    // - HCF | 0FFF | halt and catch fire.
    public final static int HCF = 0x00;
    // - BRZ rd, aa | 6daa | branch to aa on rd == 0
    public final static int BRZ = 0x6;
    // - LD rd, aa | 8daa | load rd with value of memory loc aa
    public final static int LD = 0x8;
    // - ADD rd, rs, rt | 1dst | rd <- rs + rt
    public final static int ADD = 0x1;
    // - DUMP | F000 | print out registers, machine state and memory
    public final static int DUMP = 0x0F;

    // and then
    // - BGT rd, aa | 7daa | branch to aa on rd > 0
    public final static int BGT = 7;
    // - ST rs, aa | 9saa | store rd value to memory loc aa
    public final static int ST = 9;
    // - SUB rd, rs, rt | 2dst | rd <- rs - rt
    public final static int SUB = 2;
    // - SUBI rd, rs, k | 3dsk | rd ← rs - k
    public final static int SUBI = 3;
    // - ADDI rd, rs, k | Cdsk | rd ← rs - k
    public final static int ADDI = 12;
    // - IN rd | Ad00 | read in a number to rd
    public final static int IN = 10;
    // - OUT rd | Bd00 | output a number from rd
    public final static int OUT = 11;

    // - LSH rd, rs, k | 4dsk | rd <- rs << k 
    public final static int LSH = 4;
    // - RSH rd, rs, k | 5dsk | rd <- rs >> k 
    public final static int RSH = 5;

    

    public void startAt(int initial_address)  {
        if (initial_address >= CPU.MEMORY_LIMIT) {
            throw new Panic("memory violation");
        }

        // set program counter to initial address
        // it should be an instruction to run
        cpu.set(CPU.PC, initial_address);
        
        cpu.setRunnable(); // start cpu
        while (cpu.isRunnable()) {
            // LOAD current instruction into IR
            cpu.wset(CPU.IR, cpu.fetch(cpu.get(CPU.PC)));
            // INCREMENT Program Counter
            cpu.set(CPU.PC, cpu.get(CPU.PC)+1);
            // execute current instruction
            this.decodeAndExecute(cpu.opcode(),
                cpu.arg1(), cpu.arg2(), cpu.arg3());
        }
        // on Exit
        this.cpu.dumpState();
	}

    // this is one of those pieces of code that doesn't get more "clean"
    // when you break it up into smaller and smaller implementations.
    // In the case of a whole boatload of options of what the opcode could be,
    // keeping them all in one large method is really the only thing that works.
    // Unless, heh heh, you want to do a map of functions (lambdas).
    //
    // decodeAndExecute the instruction held currently in CPU.IR
    private void decodeAndExecute(int opcode, int arg1, int arg2, int arg3) {
        switch (opcode) {
            case Engine.HLT:
                cpu.haltCPU();
                break;
            case Engine.DUMP:
                cpu.dumpState();
                break;
            case Engine.BRZ:
                branchOnZero(arg1, arg2, arg3);
                break;
            case Engine.BGT:
                branchOnGreater(arg1, arg2, arg3);
                break;
            case Engine.ADD:
                add(arg1, arg2, arg3);
                break;
            case Engine.SUB:
                subtract(arg1, arg2, arg3);
                break;
            case Engine.SUBI:
                subtractImmediate(arg1, arg2, arg3);
                break;
            case Engine.ADDI:
                addImmediate(arg1, arg2, arg3);
                break;
            
            case Engine.LD:
                loadFromMemory(arg1, arg2, arg3);
                break;
            case Engine.ST:
                storeToMemory(arg1, arg2, arg3);
                break;
                
            case Engine.IN:
                inputToReg(arg1, arg2, arg3);
                break;
            case Engine.OUT:
                outputFromReg(arg1, arg2, arg3);
                break;
            
            case Engine.LSH:
                leftShift(arg1, arg2, arg3);
                break;
            case Engine.RSH:
                rightShift(arg1, arg2, arg3);
                break;
            default:
                // perform a NOP
                ;
        }
    }
    
    // Instruction Implementations.

    private void rightShift(int arg1, int arg2, int arg3) {
        cpu.set(arg1, cpu.get(arg2) >> arg3);
    }

    private void leftShift(int arg1, int arg2, int arg3) {
        cpu.set(arg1, cpu.get(arg2) << arg3);
    }

    private void outputFromReg(int arg1, int arg2, int arg3) {
        cpu.outputInt(cpu.get(arg1));
    }

    private void inputToReg(int arg1, int arg2, int arg3) {
        cpu.set(arg1, cpu.inputInt());
    }

    private void subtractImmediate(int arg1, int arg2, int arg3) {
        cpu.set(arg1, cpu.get(arg2) - arg3);
    }

    private void addImmediate(int arg1, int arg2, int arg3) {
        cpu.set(arg1, cpu.get(arg2) + arg3);
    }

    private void subtract(int arg1, int arg2, int arg3) {
        cpu.set(arg1, cpu.get(arg2) - cpu.get(arg3));
    }

    // BGT branch on rd greater than 0
    private void branchOnGreater(int arg1, int arg2, int arg3) {
        int address = makeAddress(arg2, arg3);
        if (cpu.get(arg1) > 0) {
            cpu.set(CPU.PC, address);
        }
    }

    // ST
    private void storeToMemory(int arg1, int arg2, int arg3) {
        int address = makeAddress(arg2, arg3);
        Word tw = cpu.wget(arg1);
        try {
            cpu.store(address, tw);
        } catch (Panic e) {
            e.printStackTrace();
        }
    }

    // LD
    private void loadFromMemory(int arg1, int arg2, int arg3) {
        int address = makeAddress(arg2, arg3);
        Word tw = new Word(0);
        try {
            tw = cpu.fetch(address);
        } catch (Panic e) {
            e.printStackTrace();
        }
        cpu.wset(arg1, tw);
    }

    // ADD
    private void add(int arg1, int arg2, int arg3) {
        int t = cpu.get(arg2) + cpu.get(arg3);
        cpu.set(arg1, t);
    }

    // BRZ
    private void branchOnZero(int arg1, int arg2, int arg3) {
        int address = makeAddress(arg2, arg3);
        if (cpu.get(arg1)==0) {
            cpu.set(CPU.PC, address);
        }
    }

    // make the two argument bytes into one 16bit address.
    private int makeAddress(int arg2, int arg3) {
        int addr = ((arg2 & 0x0000FF00) | (arg3 & 0x000000FF));
        if (addr < 0 || addr >= CPU.MEMORY_LIMIT) {
            System.err.printf("Engine: Bad Address: %X \n", addr);
            throw new Panic("Engine: makeAddress: made incorrect address.");
        }
        return addr;
    }


}
