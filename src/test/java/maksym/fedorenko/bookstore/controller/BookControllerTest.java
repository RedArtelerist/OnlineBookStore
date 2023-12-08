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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.SneakyThrows;
import maksym.fedorenko.bookstore.dto.book.BookDto;
import maksym.fedorenko.bookstore.dto.book.CreateBookRequestDto;
import maksym.fedorenko.bookstore.dto.book.UpdateBookRequestDto;
import maksym.fedorenko.bookstore.dto.exception.ErrorResponseWrapper;
import maksym.fedorenko.bookstore.dto.exception.ErrorsResponseWrapper;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:database/categories/add-default-categories.sql")
@Sql(scripts = {
        "classpath:database/books/delete-all-books.sql",
        "classpath:database/categories/delete-all-categories.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
public class BookControllerTest {
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
    @DisplayName("Create valid book")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "Harry Potter",
                "J. K. Rowling",
                "1456789101",
                BigDecimal.valueOf(30),
                null,
                null,
                List.of(1L)
        );

        MvcResult result = mockMvc.perform(post("/api/books")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class
        );

        assertThat(actual).isNotNull()
                .hasFieldOrProperty("id").isNotNull()
                .hasFieldOrPropertyWithValue("title", "Harry Potter")
                .hasFieldOrPropertyWithValue("author", "J. K. Rowling")
                .hasFieldOrPropertyWithValue("isbn", "1456789101")
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(30))
                .hasFieldOrPropertyWithValue("categoryIds", Collections.singletonList(1L));
    }

    @Test
    @DisplayName("Create book without categories")
    @WithMockUser(username = "user", roles = {"USER", "ADMIN"})
    void createBook_InvalidRequestDtoWithoutCategories_BadRequest() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "Harry Potter",
                "J. K. Rowling",
                "1456789101",
                BigDecimal.valueOf(30),
                null,
                null,
                null
        );

        MvcResult result = mockMvc.perform(post("/api/books")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorsResponseWrapper errors = objectMapper.readValue(
                result.getResponse().getContentAsString(), ErrorsResponseWrapper.class
        );
        assertThat(errors.details())
                .hasSize(1)
                .element(0)
                .isEqualTo("categoryIds must not be null");
    }

    @Test
    @DisplayName("Find book by existent id")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = "classpath:database/books/add-one-book-with-categories.sql")
    void getById_WithExistentId_Success() throws Exception {
        Long id = 1L;

        MvcResult result = mockMvc.perform(get("/api/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class
        );

        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("title", "Effective Java")
                .hasFieldOrPropertyWithValue("author", "Joshua Bloch")
                .hasFieldOrPropertyWithValue("isbn", "1234567890")
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(12.99))
                .extracting(BookDto::getCategoryIds)
                .asInstanceOf(InstanceOfAssertFactories.list(Long.class))
                .containsExactlyInAnyOrderElementsOf(List.of(1L, 2L));
    }

    @Test
    @DisplayName("Find book by non-existent id")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = "classpath:database/books/add-one-book-with-categories.sql")
    void getById_WithNonExistedId_BadRequest() throws Exception {
        Long id = 100000L;

        MvcResult result = mockMvc.perform(get("/api/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponseWrapper error = objectMapper.readValue(
                result.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );
        assertThat(error.details()).isEqualTo("Can't find book by id: " + id);
    }

    @Test
    @DisplayName("Find all books")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = "classpath:database/books/add-one-book-with-categories.sql")
    void getAll_ValidRequest_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> actual = Arrays.stream(
                objectMapper.readValue(result.getResponse().getContentAsString(), BookDto[].class)
        ).toList();

        assertThat(actual)
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("title", "Effective Java")
                .hasFieldOrPropertyWithValue("author", "Joshua Bloch")
                .hasFieldOrPropertyWithValue("isbn", "1234567890")
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(12.99))
                .extracting(BookDto::getCategoryIds)
                .asInstanceOf(InstanceOfAssertFactories.list(Long.class))
                .containsExactlyInAnyOrderElementsOf(List.of(1L, 2L));
    }

    @Test
    @DisplayName("Filter books with valid parameters")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = "classpath:database/books/add-default-books-with-categories.sql")
    void searchBooks_ValidRequestDto_Success() throws Exception {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("title", "java");
        requestParams.add("minPrice", "10");
        requestParams.add("maxPrice", "20");

        MvcResult result = mockMvc.perform(get("/api/books/search")
                        .params(requestParams)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<Long> actual = Arrays.stream(
                objectMapper.readValue(result.getResponse().getContentAsString(), BookDto[].class)
        ).map(BookDto::getId).toList();

        assertThat(actual)
                .hasSize(2)
                .containsExactlyInAnyOrder(1L, 5L);
    }

    @Test
    @DisplayName("Filter books with invalid parameter price")
    @WithMockUser(username = "user", roles = "USER")
    void searchBooks_InvalidRequestDtoNegativeMinPrice_BadRequest() throws Exception {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("title", "java");
        requestParams.add("minPrice", "-10");
        requestParams.add("maxPrice", "20");

        mockMvc.perform(get("/api/books/search")
                        .params(requestParams)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("Update existent book with valid request")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @Sql(scripts = "classpath:database/books/add-one-book-with-categories.sql")
    void updateBook_ValidRequestDtoAndExistentId_Success() throws Exception {
        Long id = 1L;
        UpdateBookRequestDto requestDto = new UpdateBookRequestDto(
                "Java Puzzlers",
                null,
                null,
                BigDecimal.valueOf(30),
                null,
                null,
                List.of(3L)
        );

        MvcResult result = mockMvc.perform(put("/api/books/" + id)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class
        );

        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("title", "Java Puzzlers")
                .hasFieldOrPropertyWithValue("author", "Joshua Bloch")
                .hasFieldOrPropertyWithValue("isbn", "1234567890")
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(30))
                .hasFieldOrPropertyWithValue("categoryIds", Collections.singletonList(3L));
    }

    @Test
    @DisplayName("Delete book by existent id")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @Sql(scripts = "classpath:database/books/add-one-book-with-categories.sql")
    void deleteCategory_WithExistentId_Success() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/api/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @DisplayName("Delete book by without Admin role")
    @WithMockUser(username = "admin", roles = "USER")
    @Sql(scripts = "classpath:database/books/add-one-book-with-categories.sql")
    void deleteBook_WithoutAdminRole_Forbidden() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/api/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
