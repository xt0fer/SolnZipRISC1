package rocks.zipcode;

public class Panic extends Error {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public Panic() {
        super();
    }
    public Panic(String message) { super(message); }
    public Panic(String message, Throwable cause) { super(message, cause); }
    public Panic(Throwable cause) { super(cause); }  
}

