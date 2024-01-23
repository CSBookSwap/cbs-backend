package tech.cbs.api.repository;

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
import tech.cbs.api.repository.model.Author;
import tech.cbs.api.repository.model.Book;
import tech.cbs.api.repository.model.Level;
import tech.cbs.api.repository.model.Tag;
import tech.cbs.api.service.dto.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link BookRepository}
 */
@SpringBootTest
@Testcontainers
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private NamedParameterJdbcTemplate parameterJdbcTemplate;

    private static final List<Tag> testTags = new ArrayList<>();
    private static final List<Author> testAuthors = new ArrayList<>();
    private static final List<Book> testBooks = new ArrayList<>();
    private static final int tagCount = 15;
    private static final int authorCount = 7;
    private static final int bookCount = 30;

    private static final Random rn = new Random();

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0-alpine3.18");

    @BeforeEach
    void setData() {
        List<Author> authors = new ArrayList<>();
        for (int i = 0; i <= authorCount; i++) {
            authors.add(createAuthor());
        }
        authors.forEach(authorRepository::save);
        testAuthors.addAll(authorRepository.findAll(new Page(0, authorCount)));

        Map<String, Tag> tags = new HashMap<>();
        for (int i = 0; i <= tagCount; i++) {
            var tag = createTag();
            tags.put(tag.name(), tag);
        }
        tags.values().forEach(tagRepository::save);
        testTags.addAll(tagRepository.findAll(new Page(0, tagCount)));

        List<Book> books = new ArrayList<>();
        for (int i = 0; i <= bookCount; i++) {
            books.add(createBook());
        }
        books.forEach(bookRepository::save);
        testBooks.addAll(bookRepository.findAll(new Page(0, bookCount)));
    }

    private static Book createBook() {

        int author = rn.nextInt(testAuthors.size());
        int tagCountForBook = rn.nextInt(1, 4);

        Map<Integer, Tag> tags = new HashMap<>();

        for (int i = 0; i <= tagCountForBook; i++) {
            var tag = testTags.get(rn.nextInt(testTags.size()));
            tags.put(tag.id(), tag);
        }

        return new Book(
                0,
                "Test Title for Book #" + rn.nextInt(),
                testAuthors.get(author).id(),
                rn.nextInt(1982, 2023),
                "ISBN #" + rn.nextInt(1000000),
                Level.values()[rn.nextInt(3)],
                "Test Description #" + rn.nextInt(),
                (rn.nextBoolean()),
                new HashSet<>(tags.values())
        );
    }

    @AfterEach
    void cleanData() {
        var DELETE_ALL_SQL = """
                DELETE FROM book;
                DELETE FROM author;
                DELETE FROM tag;
                """;
        parameterJdbcTemplate.update(DELETE_ALL_SQL, Map.of());

        testAuthors.clear();
        testTags.clear();
        testBooks.clear();
    }

    @Test
    void BookRepository_FindAll_ReturnsListOfBooks() {

        List<Book> bookList = bookRepository.findAll(new Page(0, bookCount));
        assertThat(bookList).isNotNull();
        assertThat(bookList.size()).isEqualTo(bookCount);
        assertThat(bookList).containsAll(testBooks);
    }

    @Test
    void BookRepository_FindById_ReturnsBookAsOptional() {

        Book book = testBooks.get(rn.nextInt(testBooks.size()));
        Optional<Book> testBookOptional = bookRepository.findById(book.id());
        Book testBook = testBookOptional.get();

        assertThat(testBook).isNotNull();
        assertThat(testBook.id()).isEqualTo(book.id());
        assertThat(testBook.title()).isEqualTo(book.title());
        assertThat(testBook.authorId()).isEqualTo(book.authorId());
        assertThat(testBook.publicationYear()).isEqualTo(book.publicationYear());
        assertThat(testBook.isbn()).isEqualTo(book.isbn());
        assertThat(testBook.level()).isEqualTo(book.level());
        assertThat(testBook.description()).isEqualTo(book.description());
        assertThat(testBook.available()).isEqualTo(book.available());
        assertThat(testBook.tags()).isEqualTo(book.tags());
    }

    @Test
    void BookRepository_Save_ReturnsSavedBookId() {

        Book book = createBook();

        int savedBookId = bookRepository.save(book);

        Optional<Book> testBookOptional = bookRepository.findById(savedBookId);

        Book testBook = testBookOptional.get();

        assertThat(testBook).isNotNull();
        assertThat(testBook.id()).isNotZero();
        assertThat(testBook.title()).isEqualTo(book.title());
        assertThat(testBook.authorId()).isEqualTo(book.authorId());
        assertThat(testBook.publicationYear()).isEqualTo(book.publicationYear());
        assertThat(testBook.isbn()).isEqualTo(book.isbn());
        assertThat(testBook.level()).isEqualTo(book.level());
        assertThat(testBook.description()).isEqualTo(book.description());
        assertThat(testBook.available()).isEqualTo(book.available());
        assertThat(testBook.tags()).isEqualTo(book.tags());
    }

    @Test
    void BookRepository_Update_ReturnsBoolean() {

        Book bookFromDb = testBooks.get(rn.nextInt(testBooks.size()) - 1);

        Set<Tag> newTags = new HashSet<>();
        for (int i = 0; i <= rn.nextInt(4); i++) {
            newTags.add(testTags.get(rn.nextInt(testTags.size())));
        }

        Book updatedBook = new Book(
                bookFromDb.id(),
                "Updated Title",
                testAuthors.get(rn.nextInt(testAuthors.size())).id(),
                bookFromDb.publicationYear() + rn.nextInt(1, 30),
                "Updated ISBN",
                Arrays.stream(Level.values())
                        .filter(level -> !level.equals(bookFromDb.level()))
                        .toList().getFirst(),
                "Updated Description",
                !bookFromDb.available(),
                newTags
        );

        boolean result = bookRepository.update(updatedBook);

        assertThat(result).isTrue();
    }

    @Test
    void BookRepository_DeleteById_ReturnsBoolean() {

        int testBookId = testBooks.get(rn.nextInt(testBooks.size())).id();
        bookRepository.deleteById(testBookId);

        Optional<Book> testBook = bookRepository.findById(testBookId);

        assertThat(testBook).isEmpty();
    }

    @Test
    void BookRepository_FindByAuthorId_ReturnsListOfBooks() {

        int testAuthorId = testAuthors.get(rn.nextInt(testAuthors.size())).id();
        List<Book> books = bookRepository.findByAuthorId(testAuthorId);

        assertThat(books).isNotNull();
        assertThat(books.size()).isGreaterThan(0);

        books.forEach(book -> assertThat(book.authorId()).isEqualTo(testAuthorId));
    }

    @Test
    void BookRepository_FindByTagId_ReturnsListOfBooks() {
        int testTagId = testTags.get(rn.nextInt(testTags.size())).id();
        List<Book> books = bookRepository.findByTagId(testTagId, new Page(0, bookCount));

        assertThat(books).isNotNull();
        assertThat(books.size()).isGreaterThan(0);

        for (Book book : books) {
            assertThat(book.tags()
                    .stream()
                    .map(Tag::id)
                    .collect(Collectors.toList())).contains(testTagId);
        }
    }

    private static Author createAuthor() {
        return new Author(0, "Test Author fullname #" + rn.nextInt(), "test bio");
    }

    private static Tag createTag() {
        return new Tag(0, "Test Tag name #" + rn.nextInt(15000));
    }
}