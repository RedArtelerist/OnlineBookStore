package maksym.fedorenko.bookstore.exception;

public class EntityNotSavedException extends RuntimeException {
    public EntityNotSavedException(String message, Throwable cause) {
        super(message, cause);
    }
}
