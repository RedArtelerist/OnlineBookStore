package maksym.fedorenko.bookstore.exception;

public class BookNotSavedException extends RuntimeException {
    public BookNotSavedException(String message, Throwable cause) {
        super(message, cause);
    }
}
