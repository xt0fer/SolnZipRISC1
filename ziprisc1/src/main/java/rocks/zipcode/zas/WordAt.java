package rocks.zipcode.zas;

import rocks.zipcode.Word;

public class WordAt extends Word {
    private String addr;
    private String forwardref = null;

    public WordAt(String address, int bZero, int bOne, int bTwo, int bThree) {
        super(bZero, bOne, bTwo, bThree);
        this.addr = address;
    }

    public String getForwardref() {
        return forwardref;
    }

    public void setForwardref(String forwardref) {
        this.forwardref = forwardref;
    }

    public WordAt(String address, Word word) {
        super(word.getInt());
        this.addr = address;
    }

    public void undefineForwardRef(String reference) {
        this.setForwardref(reference);
        this.addr = "undefined";
    }

    public void defineForwardReference(String address) {
        this.setForwardref("");
        this.addr = address;
        int addr = Integer.decode(address);
        this.b[3] = (addr & 0xFF);
        this.b[2] = (addr & 0xFF00) >> 16;
    }
    public boolean isForwardReference() {
        return this.addr.equals("undefined");
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.addr, super.toString());
    }
}
