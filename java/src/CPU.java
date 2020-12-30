public class CPU implements RISC1Core {
    
    final static int xF = 0xF;
    private Register[] registerFile = new Register[xF];

    final static int memoryLimit = 0xFF;
    private Word[] memory = new Word[memoryLimit];

    private int inputWord = 0;
    private int outputWord = 0;
    private int statusWord = 0;
    
    public CPU() {

    }

    // CPU implementation.



    public static enum R$ {
        x0(0), x1(1), x2(2), x3(3),
        x4(4), x5(5), x6(6), x7(7),
        x8(8), x9(9), xA(10), xB(11),
        xC(12), xD(13), xE(14), xF(15), IR(14), PC(15);

        private int numVal;

        R$(int numVal) {
        this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

    @Override
    public void store(int address, Word value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fetch(int address, Word value) {
        // TODO Auto-generated method stub

    }

}
