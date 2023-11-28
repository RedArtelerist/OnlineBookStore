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
import java.util.List;
import lombok.SneakyThrows;
import maksym.fedorenko.bookstore.dto.book.BookDtoWithoutCategories;
import maksym.fedorenko.bookstore.dto.category.CategoryDto;
import maksym.fedorenko.bookstore.dto.category.CreateCategoryRequestDto;
import maksym.fedorenko.bookstore.dto.category.UpdateCategoryRequestDto;
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
@Sql(scripts = "classpath:database/categories/delete-all-categories.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class CategoryControllerTest {
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
    @DisplayName("Create valid category")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void createCategory_ValidRequestDto_Success() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "Test",
                null
        );

        MvcResult result = mockMvc.perform(post("/api/categories")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class
        );

        assertThat(actual).isNotNull()
                .hasFieldOrPropertyWithValue("name", "Test")
                .hasFieldOrPropertyWithValue("description", null)
                .extracting("id").isNotNull();
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

        ErrorResponseWrapper error = objectMapper.readValue(
                result.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );

        assertThat(error.details()).isEqualTo("Request input parameters are missing or invalid");
    }

    @Test
    @DisplayName("Find all categories")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = "classpath:database/categories/add-default-categories.sql")
    void getAll_GivenCategoriesInCatalog_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryDto> actual = Arrays.stream(objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto[].class
        )).toList();

        assertThat(actual)
                .hasSize(3)
                .element(0)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "education");
        assertThat(actual)
                .element(1)
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("name", "programming");
        assertThat(actual)
                .element(2)
                .hasFieldOrPropertyWithValue("id", 3L)
                .hasFieldOrPropertyWithValue("name", "technologies");
    }

    @Test
    @DisplayName("Find category by existent id")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = "classpath:database/categories/add-one-category.sql")
    void getById_WithExistentId_Success() throws Exception {
        Long id = 1L;

        MvcResult result = mockMvc.perform(get("/api/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class
        );

        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "education")
                .hasFieldOrPropertyWithValue("description", null);
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
    void getById_WithNonExistedId_NotFound() throws Exception {
        Long id = 100000L;

        MvcResult result = mockMvc.perform(get("/api/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponseWrapper error = objectMapper.readValue(
                result.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );
        assertThat(error.details()).isEqualTo("Can't find category by id: " + id);
    }

    @Test
    @DisplayName("Update existent category with valid request")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @Sql(scripts = "classpath:database/categories/add-one-category.sql")
    void updateCategory_ValidRequestDtoAndExistentId_Success() throws Exception {
        Long id = 1L;
        UpdateCategoryRequestDto requestDto = new UpdateCategoryRequestDto(
                "detective", "description");

        MvcResult result = mockMvc.perform(put("/api/categories/" + id)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class
        );

        assertThat(actual).hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "detective")
                .hasFieldOrPropertyWithValue("description", "description");
    }

    @Test
    @DisplayName("Delete category by existent id")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @Sql(scripts = "classpath:database/categories/add-one-category.sql")
    void deleteCategory_WithExistentId_Success() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/api/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @DisplayName("Find books by existent category id")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = {
            "classpath:database/categories/add-default-categories.sql",
            "classpath:database/books/add-default-books.sql"
    })
    @Sql(scripts = "classpath:database/books/delete-all-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void getBooksByCategoryId_WithExistentId_Success() throws Exception {
        Long bookId = 1L;

        MvcResult result = mockMvc.perform(get("/api/categories/" + bookId + "/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var actual = Arrays.stream(objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDtoWithoutCategories[].class
        )).toList();

        assertThat(actual).hasSize(3)
                .element(0)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("title", "Effective Java")
                .hasFieldOrPropertyWithValue("author", "Joshua Bloch")
                .hasFieldOrPropertyWithValue("isbn", "1234567890")
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(12.99));

        assertThat(actual)
                .element(1)
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("title", "Clean Code")
                .hasFieldOrPropertyWithValue("author", "Robert Martin")
                .hasFieldOrPropertyWithValue("isbn", "1234567891")
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(45.99));

        assertThat(actual)
                .element(2)
                .hasFieldOrPropertyWithValue("id", 5L)
                .hasFieldOrPropertyWithValue("title", "Java in a Nutshell")
                .hasFieldOrPropertyWithValue("author", "Benjamin Evans")
                .hasFieldOrPropertyWithValue("isbn", "1234567894")
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(19.99));
    }
}
