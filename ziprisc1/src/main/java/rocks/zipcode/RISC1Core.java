package rocks.zipcode;

public interface RISC1Core {

    // set/get a Word object from memory
    void store(int address, Word w) ;
    Word fetch(int address) ;

    // set a register with the contents of a Word object
    void wset(int register, Word w) ;

    default Word wget() {
         return wget();
    }

    // get a Word object from the contents of a register
    Word wget(int register) ;

    // get/set value of a register
    int get(int register) ;
    void set(int register, int i) ;

    // stops processor
    void halt();

    // get input int from standard input
    int inputInt() ;
    // output int to standard output
    void outputInt(int i);

    // get a byte from standard input
    byte inputByte() ;
    // put a byte to standard output
    void outputByte(byte i);

    // for cpu integer stack
    // popi - pops top of integer_stack to register
    void popi(int register);
    // pushi - pushes register onto top of integer stack
    void pushi(int register);
    // op(ISA op) - if possible, performs op on top two stack elements,
    // leaving the result at top of stack
    void op(ISA op);

}
