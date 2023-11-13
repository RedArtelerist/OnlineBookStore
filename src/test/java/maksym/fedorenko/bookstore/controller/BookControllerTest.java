package maksym.fedorenko.bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import maksym.fedorenko.bookstore.dto.book.BookDto;
import maksym.fedorenko.bookstore.dto.book.CreateBookRequestDto;
import maksym.fedorenko.bookstore.dto.book.UpdateBookRequestDto;
import maksym.fedorenko.bookstore.exception.ErrorResponseWrapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    protected static MockMvc mockMvc;
    private static ObjectMapper objectMapper;

    @BeforeAll
    @SneakyThrows
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/add-default-categories.sql")
            );
        }
    }

    @AfterEach
    @SneakyThrows
    void tearDown(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/delete-all-books.sql")
            );
        }
    }

    @AfterAll
    @SneakyThrows
    static void afterAll(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/delete-all-categories.sql")
            );
        }
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

        BookDto expected = new BookDto(
                null,
                requestDto.title(),
                requestDto.author(),
                requestDto.isbn(),
                requestDto.price(),
                requestDto.description(),
                requestDto.coverImage(),
                requestDto.categoryIds()
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

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Create book without categories")
    @WithMockUser(username = "user", roles = {"USER", "ADMIN"})
    void createCategory_InvalidRequestDtoWithoutCategories_BadRequest() throws Exception {
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

        String expected = "Request input parameters are missing or invalid";
        ErrorResponseWrapper error = objectMapper.readValue(
                result.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );
        assertEquals(expected, error.details());
    }

    @Test
    @DisplayName("Find book by existent id")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(
            scripts = "classpath:database/books/add-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getById_WithExistentId_Success() throws Exception {
        Long id = 1L;
        BookDto expected = new BookDto(
                id,
                "Effective Java",
                "Joshua Bloch",
                "1234567890",
                BigDecimal.valueOf(12.99),
                null,
                null,
                List.of(1L, 2L)
        );

        MvcResult result = mockMvc.perform(get("/api/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class
        );

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "categoryIds"));
    }

    @Test
    @DisplayName("Find book by nonExistent id")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(
            scripts = "classpath:database/books/add-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getById_WithNonExistedId_BadRequest() throws Exception {
        Long id = 100000L;

        MvcResult result = mockMvc.perform(get("/api/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String expected = "Can't find book by id: " + id;
        ErrorResponseWrapper error = objectMapper.readValue(
                result.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );
        assertEquals(expected, error.details());
    }

    @Test
    @DisplayName("Filter books with valid parameters")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(
            scripts = "classpath:database/books/add-default-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void searchBooks_ValidRequestDto_Success() throws Exception {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("title", "java");
        requestParams.add("minPrice", "10");
        requestParams.add("maxPrice", "20");

        List<Long> expected = List.of(1L, 5L);

        MvcResult result = mockMvc.perform(get("/api/books/search")
                        .params(requestParams)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto[].class
        );
        assertEquals(2, actual.length);
        assertEquals(
                expected,
                Arrays.stream(actual)
                        .map(BookDto::getId)
                        .toList()
        );
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
    @Sql(
            scripts = "classpath:database/books/add-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
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

        BookDto expected = new BookDto(
                id,
                "Java Puzzlers",
                "Joshua Bloch",
                "1234567890",
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

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete book by existent id")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @Sql(
            scripts = "classpath:database/books/add-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
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
    @Sql(
            scripts = "classpath:database/books/add-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void deleteBook_WithoutAdminRole_Forbidden() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/api/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
