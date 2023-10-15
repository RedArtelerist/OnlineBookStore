package maksym.fedorenko.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateBookRequestDto {
    @NotNull
    @Length(max = 128)
    private String title;

    @NotNull
    @Length(max = 128)
    private String author;

    @NotNull
    @Length(max = 128)
    private String isbn;

    @NotNull
    @Positive
    private BigDecimal price;

    @Length(max = 1024)
    private String description;

    @Length(max = 128)
    private String coverImage;
}
