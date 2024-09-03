package uma.autopsy.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CaseAlreadyExistsException extends RuntimeException {
    public CaseAlreadyExistsException(String message) {
        super(message);
    }
}

