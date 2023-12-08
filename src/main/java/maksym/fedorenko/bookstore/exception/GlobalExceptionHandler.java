package maksym.fedorenko.bookstore.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import maksym.fedorenko.bookstore.dto.exception.ErrorResponseWrapper;
import maksym.fedorenko.bookstore.dto.exception.ErrorsResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsResponseWrapper handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex
    ) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(this::getErrorMessage)
                .toList();
        return new ErrorsResponseWrapper(LocalDateTime.now(), "bad-request", errors);
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
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ErrorResponseWrapper handleRegistrationException(RegistrationException ex) {
        return new ErrorResponseWrapper(LocalDateTime.now(), "conflict", ex.getMessage());
    }

    @ExceptionHandler(value = CreateOrderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponseWrapper handleCreateOrderException(CreateOrderException ex) {
        return new ErrorResponseWrapper(LocalDateTime.now(), "bad-request", ex.getMessage());
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
