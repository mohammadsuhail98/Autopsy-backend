package uma.autopsy.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
public class UnsupportedMimeTypeException extends RuntimeException {
    public UnsupportedMimeTypeException(String message) {
        super(message);
    }
}
