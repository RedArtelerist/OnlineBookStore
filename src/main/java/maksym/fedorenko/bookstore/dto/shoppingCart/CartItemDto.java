package maksym.fedorenko.bookstore.dto.shoppingCart;

public record CartItemDto(
        Long id,
        Long bookId,
        String bookTitle,
        Integer quantity) {
}
