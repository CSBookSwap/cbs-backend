package tech.cbs.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tech.cbs.api.repository.model.Book;
import tech.cbs.api.repository.model.Level;
import tech.cbs.api.repository.model.Tag;
import tech.cbs.api.service.dto.Page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class OLDBookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;
//
//    private Author author;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0-alpine3.18");

//    @PostConstruct
//    void initData() {
//        author = authorRepository.save(new Author(0, "Test Author", "ok"));
//    }
    @Test
    void BookRepository_GetBooks_ReturnMoreThenOneBooks() {

        int bookCount = 20;

        List<Book> books = createBooks(bookCount);
        List<Tag> tags = new ArrayList<>();

        books.stream()
                .map(Book::tags)
                .peek(tagSet -> tagSet.forEach(tags::add));


        tagRepository.saveAll(tags);

        bookRepository.saveAll(books);

        List<Book> bookList = bookRepository.findAll(new Page(0, 100))
                .stream().toList();

        assertThat(bookList).isNotNull();
        assertThat(bookList.size()).isGreaterThanOrEqualTo(bookCount);

    }

    @Test
    void BookRepository_GetBook_ReturnBook() {

        Book savedBook = bookRepository.save(createBook());

        Book bookReturn = bookRepository.findById(savedBook.id()).get();

        assertThat(bookReturn).isNotNull();
    }

    @Test
    void BookRepository_Save_ReturnSavedBook() {
        Book savedBook = bookRepository.save(createBook());

        assertThat(savedBook).isNotNull();
        assertThat(savedBook.id()).isGreaterThan(0);
    }

    @Test
    void BookRepository_Update_ReturnBoolean() {
        Book originalBook = bookRepository.save(createBook());

        Book bookForUpdate = createBook();

        bookRepository.update(bookForUpdate);

        Book updatedBook = bookRepository.findById(originalBook.id()).get();

        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.id()).isEqualTo(originalBook.id());
        assertThat(updatedBook.publicationYear()).isNotEqualTo(originalBook.publicationYear());
        assertThat(updatedBook.available()).isNotEqualTo(originalBook.available());
        assertThat(updatedBook).isEqualTo(bookForUpdate);
    }

    @Test
    void BookRepository_Delete_ReturnBoolean() {

        Book savedBook = bookRepository.save(createBook());

        bookRepository.deleteById(savedBook.id());

        Optional<Book> deletedBook = bookRepository.findById(savedBook.id());

        assertThat(deletedBook).isEmpty();
    }

    private Book createBook() {

        Random rn = new Random();
        int bookId = rn.nextInt(1000 - 1 + 1) + 1;

        Set<Tag> tags = new HashSet<>();
        int tagsCount = rn.nextInt(10 - 1 + 1) + 1;

        for (int i = 0; i <= tagsCount; i++) {
            tags.add(new Tag(0, "test tag"));
        }

        return new Book(
                0,
                "Fake Title for book #" + bookId,
                0,
                2023,
                "Random isbn",
                Level.BEGINNER,
                "Fake desc",
                (bookId % 2) != 0,
                tags
        );
    }

    private List<Book> createBooks(int bookCount) {
        List<Book> books = new ArrayList<>();

        for (int i = 0; i <= bookCount; i++) {
            books.add(createBook());
        }
        return books;
    }

}