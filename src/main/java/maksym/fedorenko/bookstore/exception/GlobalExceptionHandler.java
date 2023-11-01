package maksym.fedorenko.bookstore.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseWrapper handleInvalidArguments(
            MethodArgumentNotValidException ex, WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();
        log.warn("Validation errors for {}: {}", requestUri, errors);
        return new ErrorResponseWrapper(LocalDateTime.now(), "bad-request",
                "Request input parameters are missing or invalid"
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponseWrapper handle(ConstraintViolationException ex) {
        return new ErrorResponseWrapper(LocalDateTime.now(), "bad-request", ex.getMessage());
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

    @ExceptionHandler(value = AuthenticationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponseWrapper handleAuthenticationException(AuthenticationException ex) {
        return new ErrorResponseWrapper(LocalDateTime.now(), "bad-request", ex.getMessage());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ErrorResponseWrapper handleAccessDeniedException(AccessDeniedException ex) {
        return new ErrorResponseWrapper(LocalDateTime.now(), "forbidden", ex.getMessage());
    }

    @ExceptionHandler(value = RegistrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponseWrapper handleRegistrationException(RegistrationException ex) {
        return new ErrorResponseWrapper(LocalDateTime.now(), "bad-request", ex.getMessage());
    }

    @ExceptionHandler(value = CreateOrderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponseWrapper handleCreateOrderException(CreateOrderException ex) {
        return new ErrorResponseWrapper(LocalDateTime.now(), "bad-request", ex.getMessage());
    }
}
