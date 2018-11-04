package be.kdg.processor.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author C&eacute;dric Goffin
 */

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserException extends Throwable {
    public UserException(String message) {
        super(message);
    }
}
