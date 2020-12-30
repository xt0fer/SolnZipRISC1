import java.io.IOException;

public class CPU implements RISC1Core {

    final static int xF = 0xF;
    private Register[] registerFile = new Register[xF];

    final static int memoryLimit = 0xFF;
    private Word[] memory = new Word[memoryLimit];

    private int inputWord = 0;
    private int outputWord = 0;
    int statusWord = 0;

    public CPU() {

    }

    // CPU implementation.

    public static enum R$ {
        x0(0), x1(1), x2(2), x3(3), x4(4), x5(5), x6(6), x7(7), x8(8), x9(9), xA(10), xB(11), xC(12), xD(13), xE(14),
        xF(15), IR(14), PC(15);

        private int numVal;

        R$(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

    private void checkAddress(int address) throws PanicException {
        if (address < 0 || address > memoryLimit)
            throw new PanicException("address out of range");
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
        this.set(register, i);
    }

    @Override
    public Word wget(int register) {
        Word w = new Word(this.get(register));
        return w;
    }

    @Override
    public int get(int r) {
        if (r == 0)
            return 0;
        return this.registerFile[r].r;
    }

    @Override
    public void set(int r, int i) {
        if (r == 0)
            return;
        this.registerFile[r].r = i;
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

}
