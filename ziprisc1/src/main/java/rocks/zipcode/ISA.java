package rocks.zipcode;

import java.util.HashMap;
import java.util.Map;

// This is the Instruction Set file. This ENUM holds each of the
// Instruction (a string) to an OpCode (an int)
// lookupTable is the Map from Instruction(key) to OpCode(value)
//
public enum ISA {
    // opcodes - Instruction Set
    // - HLT | 0000 | halt.
    HLT(0x00),

    // ADDs
    // - ADD rd, rs, rt | 1dst | rd <- rs + rt
    ADD(1),
    // - ADDI rd, rs, k | Cdsk | rd ← rs + k ; k needs to be #k like #4 
    ADDI(2),

    // SUBs
    // - SUB rd, rs, rt | 2dst | rd <- rs - rt
    SUB(3),
    // - SUBI rd, rs, k | 3dsk | rd ← rs - k ; k needs to be #k like #4 
    SUBI(4),

    // - BRZ rd, aa | 6daa | branch to aa on rd == 0
    BRZ(5),
    // - BGT rd, aa | 7daa | branch to aa on rd > 0
    BGT(6),

    // logical operators
    // - LSH rd, rs, k | 4dsk | rd <- rs << k 
    LSH(7),
    // - RSH rd, rs, k | 5dsk | rd <- rs >> k 
    RSH(8),
    // - AND rd, rs, rt 
    AND(9),
    // - OR rd, rs, rt | A
    OR(0xA),
    // - XOR rd, rs, rt | B
    XOR(0xB),

    // LD rd, aa | Cdaa | load rd with value of memory loc aa
    LD(0xC),
    // LDI rd, aa | Ddaa | load rd with address aa
    LDI(0xD),
    // - ST rs, aa | Esaa | store rd value to memory loc aa
    ST(0xE),
    // LDR rd, rs |F| load rd with contents of memory(rs)
    LDR(0xF),
    // STR rd, rs | 10 | store rd with contents of memory(rs)
    STR(0x10),

    // - IN rd | 0x11d00 | read in a integer to rd
    IN(0x11),
    // - OUT rd |0x12d00 | output a integer from rd
    OUT(0x12),
    // INB rd | 0x13d00 | read a byte from stdin
    INB(0x13),
    // OUTB rd | 0x14d00 | write a byte to stdout
    OUTB(0x14),

    // hole: 0x15, 0x16

    // more branches for completeness
    // BLT  rd, aa | 0x17daa | branch to aa if rd less than 0
    BLT(0x17),
    // BRNZ rd, aa | 0x18daa | branch to aa if rd NOT equal to zero
    BRNZ(0x18),
    // BLE rd, aa | 0x19daa | branch to aa if rd less than or equal to zero
    BLE(0x19),

    // - DUMP | F000 | print out registers, machine state and memory
    DUMP(0x1F), // 31

    // - HCF | 0x20FFF | halt and catch fire.
    HCF(0x20), // Halt and Catch Fire (unimplemented instruction)
    // - Comparators
    // - CMEQ rd, rs, rt | rd <- 1 if rs == rt, 0 otherwise
    CMEQ(0x21),
    // - CMNE rd, rs, rt | rd <- 1 if rs != rt, 0 otherwise
    CMNE(0x22),
    // - CMLT rd, rs, rt | rd <- 1 if rs < rt, 0 otherwise
    CMLT(0x23),
    // - CMGE rd, rs, rt | rd <- 1 if rs >= rt, 0 otherwise
    CMGE(0x24),
    // zas will also emit CMGR and CMLE by swapping the source regs.
    // because: x > y is also y <= x (right?)
    // because: x <= y is also y > x (right?)
    ;


    private final int opcode;
    private ISA(int opcode){
        this.opcode = opcode;
    }

    private static final Map<Integer, ISA> lookupTable = new HashMap<Integer, ISA>();

    static {
        for (ISA d : ISA.values()) {
            lookupTable.put(d.getOpcode(), d);
        }
    }

    public int getOpcode() {
        return this.opcode;
    }
    public static ISA getISA(int v){
        return lookupTable.get(v);
    }

    //public abstract String getOutputFormat();
}
