package tech.cbs.api.repository;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tech.cbs.api.repository.model.Book;
import tech.cbs.api.service.dto.Page;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private TagRepository tagRepository;

    private Page page = new Page(0, 100);

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0-alpine3.18");

    @Before
    void setUp() {

    }
    @Test
    void findAll() {

        int bookCount = 20;

        bookRepository.saveAll(createBooks(bookCount));

        List<Book> bookList = bookRepository.findAll(page);
        assertThat(bookList).isNotNull();
        assertThat(bookList.size()).isGreaterThanOrEqualTo(bookCount);
    }

    @Test
    void findById() {
    }

    @Test
    void save() {
    }

    @Test
    void saveAll() {
    }

    @Test
    void update() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void findByAuthorId() {
    }

    @Test
    void findByTagId() {
    }

    @Test
    void findByIds() {
    }


    private Book createBook() {
return null;
    }

    private List<Book> createBooks(int bookCount) {
        List<Book> books = new ArrayList<>();

        for (int i = 0; i <= bookCount; i++) {
            books.add(createBook());
        }
        return books;
    }
}