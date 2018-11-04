package be.kdg.processor.fine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author C&eacute;dric Goffin
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class FineException extends Throwable {
    public FineException(String message) {
        super(message);
    }
}
