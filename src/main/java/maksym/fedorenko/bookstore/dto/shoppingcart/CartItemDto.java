package maksym.fedorenko.bookstore.dto.shoppingcart;

public record CartItemDto(
        Long id,
        Long bookId,
        String bookTitle,
        Integer quantity) {
}
