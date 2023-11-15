package maksym.fedorenko.bookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import maksym.fedorenko.bookstore.dto.shoppingcart.CartDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.CartItemDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.CreateCartItemRequestDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.UpdateCartItemRequestDto;
import maksym.fedorenko.bookstore.exception.ErrorResponseWrapper;
import org.assertj.core.api.InstanceOfAssertFactories;
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
        MvcResult result = mockMvc.perform(get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CartDto.class
        );

        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", 1L)
                .extracting(CartDto::cartItems)
                .asInstanceOf(InstanceOfAssertFactories.list(CartItemDto.class))
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("bookId", 1L)
                .hasFieldOrPropertyWithValue("bookTitle", "Effective Java")
                .hasFieldOrPropertyWithValue("quantity", 1);
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

        ErrorResponseWrapper error = objectMapper.readValue(
                result.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );

        assertThat(error.details()).startsWith("Can't find cart for user with email");
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

        MvcResult result = mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        CartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CartDto.class
        );

        assertThat(actual).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .extracting(CartDto::cartItems)
                .asInstanceOf(InstanceOfAssertFactories.list(CartItemDto.class))
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("bookId", 1L)
                .hasFieldOrPropertyWithValue("bookTitle", "Effective Java")
                .hasFieldOrPropertyWithValue("quantity", 2);
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

        ErrorResponseWrapper error = objectMapper.readValue(
                result.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );

        assertThat(error.details()).isEqualTo("Can't find book by id: %d".formatted(bookId));
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

        MvcResult result = mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        CartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CartDto.class
        );

        assertThat(actual).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .extracting(CartDto::cartItems)
                .asInstanceOf(InstanceOfAssertFactories.list(CartItemDto.class))
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("bookId", 1L)
                .hasFieldOrPropertyWithValue("bookTitle", "Effective Java")
                .hasFieldOrPropertyWithValue("quantity", 3);
    }

    @Test
    @DisplayName("Update cart item by existent id")
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    @Sql(scripts = "classpath:database/shopping_cart/add-one-item.sql")
    public void updateCartItem_ValidRequestDtoWithExistentId_Success() throws Exception {
        Long id = 1L;
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(2);

        MvcResult result = mockMvc.perform(put("/api/cart/cart-items/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        CartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CartDto.class
        );

        assertThat(actual).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .extracting(CartDto::cartItems)
                .asInstanceOf(InstanceOfAssertFactories.list(CartItemDto.class))
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("bookId", 1L)
                .hasFieldOrPropertyWithValue("bookTitle", "Effective Java")
                .hasFieldOrPropertyWithValue("quantity", 2);
    }

    @Test
    @DisplayName("Delete cart item by existing id")
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    @Sql(scripts = "classpath:database/shopping_cart/add-one-item.sql")
    public void deleteCartItem_WithExistentId_Success() throws Exception {
        Long id = 1L;

        MvcResult result = mockMvc.perform(delete("/api/cart/cart-items/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CartDto.class
        );

        assertThat(actual).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .extracting(CartDto::cartItems)
                .asInstanceOf(InstanceOfAssertFactories.list(CartItemDto.class))
                .hasSize(0);
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

        ErrorResponseWrapper error = objectMapper.readValue(
                result.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );

        assertThat(error.details())
                .startsWith("Can't find cart item with id=%d in user's cart with email"
                        .formatted(id));
    }
}
