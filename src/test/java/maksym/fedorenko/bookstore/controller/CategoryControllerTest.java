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
import maksym.fedorenko.bookstore.dto.book.BookDtoWithoutCategories;
import maksym.fedorenko.bookstore.dto.category.CategoryDto;
import maksym.fedorenko.bookstore.dto.category.CreateCategoryRequestDto;
import maksym.fedorenko.bookstore.dto.category.UpdateCategoryRequestDto;
import maksym.fedorenko.bookstore.exception.ErrorResponseWrapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
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
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
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

        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/add-default-categories.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @SneakyThrows
    static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/delete-default-categories.sql")
            );
        }
    }

    @Test
    @DisplayName("Create valid category")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @Sql(
            scripts = "classpath:database/categories/delete-test-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void createCategory_ValidRequestDto_Success() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "Test",
                null
        );

        CategoryDto expected = new CategoryDto(null, "Test", null);

        MvcResult result = mockMvc.perform(post("/api/categories")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class
        );

        assertNotNull(actual);
        assertNotNull(actual.id());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Create category with null name")
    @WithMockUser(username = "user", roles = {"USER", "ADMIN"})
    void createCategory_InvalidRequestDtoWithNullName_BadRequest() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                null, null);

        MvcResult result = mockMvc.perform(post("/api/categories")
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
    @DisplayName("Find all categories")
    @WithMockUser(username = "user", roles = "USER")
    void getAll_GivenCategoriesInCatalog_Success() throws Exception {
        List<CategoryDto> expected = List.of(
                new CategoryDto(1L, "education", null),
                new CategoryDto(2L, "programming", null),
                new CategoryDto(3L, "technologies", null)
        );
        
        MvcResult result = mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto[].class
        );

        assertEquals(3, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @DisplayName("Find category by existent id")
    @WithMockUser(username = "user", roles = "USER")
    void getById_WithExistentId_Success() throws Exception {
        Long id = 1L;
        CategoryDto expected = new CategoryDto(id, "education", null);

        MvcResult result = mockMvc.perform(get("/api/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class
        );

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Find category by invalid id")
    @WithMockUser(username = "user", roles = "USER")
    void getById_WithInvalidId_BadRequest() throws Exception {
        Long id = -1L;

        mockMvc.perform(get("/api/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("Find category by nonExistent id")
    @WithMockUser(username = "user", roles = "USER")
    void getById_WithNonExistedId_BadRequest() throws Exception {
        Long id = 100000L;

        MvcResult result = mockMvc.perform(get("/api/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String expected = "Can't find category by id: " + id;
        ErrorResponseWrapper error = objectMapper.readValue(
                result.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );
        assertEquals(expected, error.details());
    }

    @Test
    @DisplayName("Update existent category with valid request")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @Sql(
            scripts = "classpath:database/categories/add-category-for-update-or-delete.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/categories/delete-updated-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void updateCategory_ValidRequestDtoAndExistentId_Success() throws Exception {
        Long id = 4L;
        UpdateCategoryRequestDto requestDto = new UpdateCategoryRequestDto(
                "detective", "description");

        CategoryDto expected = new CategoryDto(4L, "detective", "description");

        MvcResult result = mockMvc.perform(put("/api/categories/" + id)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class
        );

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete category by existent id")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @Sql(
            scripts = "classpath:database/categories/add-category-for-update-or-delete.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/categories/delete-updated-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void deleteCategory_WithExistentId_Success() throws Exception {
        Long id = 4L;
        mockMvc.perform(delete("/api/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @DisplayName("Find books by existent category id")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(
            scripts = "classpath:database/books/add-default-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/books/delete-default-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void getBooksByCategoryId_WithExistentId_Success() throws Exception {
        Long id = 1L;
        List<BookDtoWithoutCategories> expected = List.of(
                new BookDtoWithoutCategories(
                        1L, "Effective Java", "Joshua Bloch", "1234567890",
                        new BigDecimal("12.99"), null, null
                ),
                new BookDtoWithoutCategories(
                        2L, "Clean Code", "Robert Martin", "1234567891",
                        new BigDecimal("45.99"), null, null
                ),
                new BookDtoWithoutCategories(
                        5L, "Java in a Nutshell", "Benjamin Evans", "1234567894",
                        new BigDecimal("19.99"), null, null
                )
        );

        MvcResult result = mockMvc.perform(get("/api/categories/" + id + "/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDtoWithoutCategories[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDtoWithoutCategories[].class
        );

        assertEquals(3, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }
}
