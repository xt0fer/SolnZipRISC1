

public interface RISC1Core {

     void store(int address, Word value);
     void fetch(int address, Word value);
     void set(int register, Word value);
     void get(int register, Word value);
     void halt();
     
}
