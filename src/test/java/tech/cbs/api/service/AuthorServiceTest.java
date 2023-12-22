package tech.cbs.api.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tech.cbs.api.exception.ResourceNotFoundException;
import tech.cbs.api.repository.AuthorRepository;
import tech.cbs.api.service.dto.AuthorDto;
import tech.cbs.api.service.dto.Page;
import tech.cbs.api.service.mapper.AuthorMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for {@link AuthorService}
 */
@SpringBootTest
@Testcontainers
class AuthorServiceTest {


    private static final List<AuthorDto> testAuthors = new ArrayList<>();
    private static final int authorCount = 30;
    private static final Random rn = new Random();

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0-alpine3.18");
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private NamedParameterJdbcTemplate parameterJdbcTemplate;

    private static AuthorDto createTestAuthor() {
        return new AuthorDto(0, "Test Author fullname #" + rn.nextInt(), "test bio #" + rn.nextInt());
    }


    @BeforeEach
    void setData() {
        List<AuthorDto> authors = new ArrayList<>();
        for (int i = 0; i <= authorCount; i++) {
            authors.add(createTestAuthor());
        }
        authors.stream()
                .map(AuthorMapper::toModel)
                .map(authorRepository::save)
                .map(authorRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(AuthorMapper::toDto)
                .forEach(testAuthors::add);
    }

    @AfterEach
    void clearData() {
        parameterJdbcTemplate.update("DELETE FROM author;", Map.of());
        testAuthors.clear();
    }

    @Test
    void AuthorService_GetAuthors_ReturnsListOfAuthorDto() {

        List<AuthorDto> authors = authorService.getAuthors(new Page(0, authorCount));
        assertThat(authors).isNotNull();
        assertThat(authors).isNotEmpty();
        assertThat(authors.size()).isEqualTo(authorCount);
    }

    @Test
    void AuthorService_GetAuthor_ReturnsAuthorDto() {
        AuthorDto testAuthor = testAuthors.get(rn.nextInt(0, authorCount - 1));
        AuthorDto author = authorService.getAuthor(testAuthor.id());

        assertThat(author).isNotNull();
        assertThat(author.id()).isEqualTo(testAuthor.id());
        assertThat(author.name()).isEqualTo(testAuthor.name());
        assertThat(author.biography()).isEqualTo(testAuthor.biography());
    }

    @Test
    void AuthorService_CreateAuthor_ReturnsAuthorDto() {
        AuthorDto testAuthor = createTestAuthor();
        AuthorDto author = authorService.createAuthor(testAuthor);

        assertThat(author).isNotNull();
        assertThat(author.id()).isNotEqualTo(0);
        assertThat(author.name()).isEqualTo(testAuthor.name());
        assertThat(author.biography()).isEqualTo(testAuthor.biography());
    }

    @Test
    void AuthorService_UpdateAuthor_ReturnsResultAsBoolean() {
        AuthorDto testAuthor = testAuthors.get(rn.nextInt(0, authorCount - 1));

        AuthorDto updatedAuthor = new AuthorDto(
                testAuthor.id(),
                testAuthor.name() + " updated",
                testAuthor.biography() + " updated"
        );

        boolean result = authorService.updateAuthor(updatedAuthor);

        assertThat(result).isTrue();

        AuthorDto author = authorService.getAuthor(testAuthor.id());

        assertThat(author).isNotNull();
        assertThat(author.id()).isEqualTo(testAuthor.id());
        assertThat(author.name()).isEqualTo(updatedAuthor.name());
        assertThat(author.biography()).isEqualTo(updatedAuthor.biography());
    }

    @Test
    void AuthorService_DeleteAuthor_ReturnsResultAsBoolean() {
        AuthorDto testAuthor = testAuthors.get(rn.nextInt(0, authorCount - 1));

        boolean result = authorService.deleteAuthor(testAuthor.id());

        assertThat(result).isTrue();

        Throwable thrown = assertThrows(ResourceNotFoundException.class, () -> authorService.getAuthor(testAuthor.id()));
        assertThat(thrown.getMessage()).isNotNull();

    }
}