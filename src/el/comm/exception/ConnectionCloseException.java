package el.comm.exception;

public class ConnectionCloseException extends Exception {
    public ConnectionCloseException() {
        super("connection already close!");
    }
}
