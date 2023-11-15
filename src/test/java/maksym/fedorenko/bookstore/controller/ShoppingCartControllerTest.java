package maksym.fedorenko.bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Collections;
import lombok.SneakyThrows;
import maksym.fedorenko.bookstore.dto.shoppingcart.CartDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.CartItemDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.CreateCartItemRequestDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.UpdateCartItemRequestDto;
import maksym.fedorenko.bookstore.exception.ErrorResponseWrapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:database/auth/add-default-users.sql")
@Sql(scripts = {
        "classpath:database/shopping_cart/delete-all-items.sql",
        "classpath:database/auth/delete-users.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
public class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    private static ObjectMapper objectMapper;

    @BeforeAll
    @SneakyThrows
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }


    @Test
    @DisplayName("Get shopping cart by existing user")
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    @Sql(scripts = "classpath:database/shopping_cart/add-one-item.sql")
    public void getUserCart_WithExistingUser_Success() throws Exception {
        CartDto expected = new CartDto(1L,
                Collections.singletonList(
                        new CartItemDto(1L, 1L, "Effective Java", 1)
                )
        );

        MvcResult result = mockMvc.perform(get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CartDto.class
        );

        // @TODO use AssertJS

        assertEquals(1L, actual.cartItems().size());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get shopping cart by non-existent user")
    @WithMockUser(username = "another_user@gmail.com", roles = "USER")
    @Sql(scripts = "classpath:database/shopping_cart/add-one-item.sql")
    public void getUserCart_WithNonExistentUser_BadRequest() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String expected = "Can't find cart for user with email";
        ErrorResponseWrapper error = objectMapper.readValue(
                result.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );

        assertTrue(error.details().startsWith(expected));
    }

    @Test
    @DisplayName("Add new cart item")
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    @Sql(scripts = "classpath:database/books/add-one-book.sql")
    public void addCartItem_ValidRequestDto_Success() throws Exception {
        Long bookId = 1L;
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                bookId, 2
        );

        CartDto expected = new CartDto(1L, Collections.singletonList(
                new CartItemDto(1L, bookId, "Effective Java", requestDto.quantity()))
        );

        MvcResult result = mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        CartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CartDto.class
        );

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Add cart item with non-existent book")
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    @Sql(scripts = "classpath:database/books/add-one-book.sql")
    public void addCartItem_InvalidRequestDtoNonExistentBook_BadRequest() throws Exception {
        Long bookId = 2L;
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                bookId, 2
        );

        MvcResult result = mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andReturn();

        String expected = "Can't find book by id: %d".formatted(bookId);
        ErrorResponseWrapper error = objectMapper.readValue(
                result.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );

        assertEquals(expected, error.details());
    }

    @Test
    @DisplayName("Add item that already exist in cart ")
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    @Sql(scripts = "classpath:database/shopping_cart/add-one-item.sql")
    public void addCartItem_ValidRequestDtoWithExistentBookInCart_Success() throws Exception {
        Long bookId = 1L;
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                bookId, 2
        );

        CartDto expected = new CartDto(1L, Collections.singletonList(
                new CartItemDto(1L, bookId, "Effective Java", 3))
        );

        MvcResult result = mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        CartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CartDto.class
        );

        assertEquals(1L, actual.cartItems().size());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update cart item by existent id")
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    @Sql(scripts = "classpath:database/shopping_cart/add-one-item.sql")
    public void updateCartItem_ValidRequestDtoWithExistentId_Success() throws Exception {
        Long id = 1L;
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(2);

        CartDto expected = new CartDto(1L, Collections.singletonList(
                new CartItemDto(id, 1L, "Effective Java", 2))
        );

        MvcResult result = mockMvc.perform(put("/api/cart/cart-items/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        CartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CartDto.class
        );

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete cart item by existing id")
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    @Sql(scripts = "classpath:database/shopping_cart/add-one-item.sql")
    public void deleteCartItem_WithExistentId_Success() throws Exception {
        Long id = 1L;

        CartDto expected = new CartDto(1L, Collections.emptyList());

        MvcResult result = mockMvc.perform(delete("/api/cart/cart-items/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CartDto.class
        );

        assertEquals(0, actual.cartItems().size());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete cart item that does not belong to the user")
    @WithMockUser(username = "admin@gmail.com", roles = "USER")
    @Sql(scripts = "classpath:database/shopping_cart/add-one-item.sql")
    public void deleteCartItem_DoesNtBelongThisUser_BadRequest() throws Exception {
        Long id = 1L;

        MvcResult result = mockMvc.perform(delete("/api/cart/cart-items/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String expected = "Can't find cart item with id=%d in user's cart with email"
                .formatted(id);

        ErrorResponseWrapper error = objectMapper.readValue(
                result.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );

        assertTrue(error.details().startsWith(expected));
    }
}
