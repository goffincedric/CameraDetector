package be.kdg.processor.fine.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author C&eacute;dric Goffin
 */
@ControllerAdvice
public class FineExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {FineException.class})
    protected ResponseEntity<?> handleFinesNotFound(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, "Fine(s) not found!",
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request);
    }
}
