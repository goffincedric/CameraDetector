package be.kdg.processor.configuration;

import be.kdg.processor.fine.exceptions.FineException;
import be.kdg.processor.user.exceptions.UserException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * ControllerAdvice class for web controllers to handle exceptions
 *
 * @author C&eacute;dric Goffin
 */
@ControllerAdvice
public class WebExceptionHandler extends ResponseEntityExceptionHandler {
    //TODO: Redirect to error page?
    @ExceptionHandler(value = {FineException.class})
    protected ResponseEntity<?> handleFinesNotFound(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, "Fine(s) not found!",
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(value = {UserException.class})
    protected ResponseEntity<?> handleUserExeption(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, null,
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request);
    }
}
