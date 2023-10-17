package maksym.fedorenko.bookstore.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidArguments(
            MethodArgumentNotValidException ex, WebRequest request
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .toList();
        body.put("error", "bad-request");
        body.put("time", LocalDateTime.now());
        body.put("errors", errors);

        String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();
        log.debug("Validation errors for {}: {}", requestUri, errors);
        return new ResponseEntity<Object>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponseWrapper handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ErrorResponseWrapper(LocalDateTime.now(), "entity-not-found", ex.getMessage());
    }

    @ExceptionHandler(value = EntityNotSavedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponseWrapper handleEntityNotSavedException(EntityNotSavedException ex) {
        return new ErrorResponseWrapper(LocalDateTime.now(), "entity-not-saved", ex.getMessage());
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError) {
            String field = ((FieldError) e).getField();
            String message = e.getDefaultMessage();
            return field + " " + message;
        }
        return e.getDefaultMessage();
    }
}
