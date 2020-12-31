package rocks.zipcode.zas;

import rocks.zipcode.Word;

public class WordAt extends Word {
    private String addr;

    public WordAt(String address, int bZero, int bOne, int bTwo, int bThree) {
        super(bZero, bOne, bTwo, bThree);
        this.addr = address;
    }
    public WordAt(String address, Word word) {
        super(word.getInt());
        this.addr = address;
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.addr, super.toString());
    }
}
