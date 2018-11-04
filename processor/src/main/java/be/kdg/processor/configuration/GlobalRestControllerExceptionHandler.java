package be.kdg.processor.configuration;

import be.kdg.processor.fine.exceptions.FineException;
import be.kdg.processor.user.exceptions.UserException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Global ControllerAdvice class for all REST controllers to handle exceptions
 *
 * @author C&eacute;dric Goffin
 */
@RestControllerAdvice
public class GlobalRestControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {FineException.class, UserException.class})
    protected ResponseEntity<?> handleFinesNotFound(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request);
    }
}
