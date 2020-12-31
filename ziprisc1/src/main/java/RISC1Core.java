

public interface RISC1Core {

     void store(int address, Word w) ;
     Word fetch(int address) ;

     void wset(int register, Word w) ;

    default Word wget() {
         return wget();
     }

    Word wget(int register) ;

     int get(int register) ;
     void set(int register, int i) ;

    void halt();

    int inputInt() ;
    void outputInt(int i);

}
