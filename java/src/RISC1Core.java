

public interface RISC1Core {

     void store(int address, Word w) throws PanicException;
     Word fetch(int address) throws PanicException;

     void wset(int register, Word w) throws PanicException;
     Word wget(int register) throws PanicException;

     int get(int register) throws PanicException;
     void set(int register, int i) throws PanicException;

     void halt();

     int inputInt();
     void outputInt(int i);

}
