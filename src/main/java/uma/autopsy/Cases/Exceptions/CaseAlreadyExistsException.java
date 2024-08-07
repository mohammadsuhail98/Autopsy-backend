package uma.autopsy.Cases.Exceptions;

public class CaseAlreadyExistsException extends RuntimeException {
    public CaseAlreadyExistsException(String message) {
        super(message);
    }
}

