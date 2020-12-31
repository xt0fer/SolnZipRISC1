
public class PanicException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public PanicException() {
        super();
    }
    public PanicException(String message) { super(message); }
    public PanicException(String message, Throwable cause) { super(message, cause); }
    public PanicException(Throwable cause) { super(cause); }  
}

