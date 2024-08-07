package uma.autopsy.Cases.Exceptions;

public class CaseDoesNotExistException extends RuntimeException {
    public CaseDoesNotExistException(String message) {
        super(message);
    }
}
