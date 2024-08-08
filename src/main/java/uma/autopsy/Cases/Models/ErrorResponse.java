package uma.autopsy.Cases.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Setter
public class ErrorResponse {
    private int statusCode;
    private String error;
    private Timestamp timestamp;
    private String message;

    public ErrorResponse(int statucCode, String error, String message) {
        super();
        this.statusCode = statucCode;
        this.error = error;
        this.message = message;
        this.timestamp = Timestamp.from(Instant.now());;
    }
}