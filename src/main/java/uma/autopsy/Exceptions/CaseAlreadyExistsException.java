package uma.autopsy.Exceptions;

public class CaseAlreadyExistsException extends RuntimeException {
    public CaseAlreadyExistsException(String message) {
        super(message);
    }
}

