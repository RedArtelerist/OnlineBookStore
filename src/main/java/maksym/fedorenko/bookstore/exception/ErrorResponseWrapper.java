package maksym.fedorenko.bookstore.exception;

import java.time.LocalDateTime;

public record ErrorResponseWrapper(LocalDateTime time, String error, String details) {
}
