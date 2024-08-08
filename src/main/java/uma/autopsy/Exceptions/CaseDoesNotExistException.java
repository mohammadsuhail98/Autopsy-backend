package uma.autopsy.Exceptions;

public class CaseDoesNotExistException extends RuntimeException {
    public CaseDoesNotExistException(String message) {
        super(message);
    }
}
