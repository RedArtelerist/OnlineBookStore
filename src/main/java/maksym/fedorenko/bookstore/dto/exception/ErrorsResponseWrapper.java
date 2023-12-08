package maksym.fedorenko.bookstore.dto.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorsResponseWrapper(LocalDateTime time, String error, List<String> details) {
}
