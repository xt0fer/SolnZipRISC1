package rocks.zipcode;

import java.util.HashMap;
import java.util.Map;

public enum ISA {
    // opcodes - Instruction Set
    // - HLT | 0000 | halt.
    HLT(0x00),
    // - HCF | 0FFF | halt and catch fire.
    // HCF(0x00),

    // ADDs
    // - ADD rd, rs, rt | 1dst | rd <- rs + rt
    ADD(1),
    // - ADDI rd, rs, k | Cdsk | rd ← rs - k
    ADDI(2),

    // SUBs
    // - SUB rd, rs, rt | 2dst | rd <- rs - rt
    SUB(3),
    // - SUBI rd, rs, k | 3dsk | rd ← rs - k
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
    OR(10),
    // - XOR rd, rs, rt | B
    XOR(11),

    // - LD rd, aa | Cdaa | load rd with value of memory loc aa
    LD(12),
    // - ST rs, aa | Dsaa | store rd value to memory loc aa
    ST(13),
    // LDR |E|- load reg indirect
    LDR(14),
    // STR | F |- store reg indirect
    STR(15),

    // - IN rd | 0x10d00 | read in a integer to rd
    IN(16),
    // - OUT rd |0x11d00 | output a integer from rd
    OUT(17),
    // INB rd | 0x12d00 | read a byte from stdin
    INB(18),
    // OUTB rd | 0x13d00 | write a byte to stdout
    OUTB(19),

    // hole: 20, 21, 22

    // more branches for completeness
    // BLT  rd, aa | 0x17daa | branch to aa if rd less than 0
    BLT(23),
    // BRNZ rd, aa | 0x18daa | branch to aa if rd NOT equal to zero
    BRNZ(24),
    // BLE rd, aa | 0x19daa | branch to aa if rd less than or equal to zero
    BLE(25),

    // - DUMP | F000 | print out registers, machine state and memory
    DUMP(0x1F), // 31
    ;


    private final int opcode;
    private ISA(int opcode){
        this.opcode = opcode;
    }

    private static final Map<Integer, ISA> lookup = new HashMap<Integer, ISA>();

    static {
        for (ISA d : ISA.values()) {
            lookup.put(d.getOpcode(), d);
        }
    }

    public int getOpcode() {
        return this.opcode;
    }
    public static ISA getISA(int v){
        return lookup.get(v);
    }

    //public abstract String getOutputFormat();
}
