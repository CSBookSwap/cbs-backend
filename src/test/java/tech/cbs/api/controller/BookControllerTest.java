package tech.cbs.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tech.cbs.api.repository.model.Level;
import tech.cbs.api.service.BookService;
import tech.cbs.api.service.dto.AuthorDto;
import tech.cbs.api.service.dto.BookDto;
import tech.cbs.api.service.dto.Page;
import tech.cbs.api.service.dto.TagDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;

    private final static int booksCount = 25;
    private final static int authorsCount = 5;
    private final static List<AuthorDto> testAuthors = new ArrayList<>();
    private final static int tagsCount = 10;
    private final static List<TagDto> testTags = new ArrayList<>();
    private final static Random rn = new Random();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    void BookController_GetBooks_ReturnsListOfBooks() throws Exception {
        var books = createBooks();
        doReturn(books).when(this.bookService).getBooks(new Page(0, booksCount));

        var responseEntity = this.bookController.getBooks(0, booksCount);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(books);
    }

    @Test
    void BookController_GetBook_ReturnsBook() throws Exception {
        var book = createBook(100);
        doReturn(book).when(this.bookService).getBook(book.id());

        var responseEntity = this.bookController.getBook(book.id());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(book);
    }

    @Test
    void BookController_GetBooksByAuthor_ReturnsListOfBooks() throws Exception {
        var books = createBooks();
        var author = testAuthors.get(rn.nextInt(0, authorsCount - 1));
        var bookByAuthor = books.stream()
                .filter(book -> book.authorId() == author.id())
                .collect(Collectors.toList());
        doReturn(bookByAuthor).when(this.bookService).getBooksByAuthor(author.id());

        var responseEntity = this.bookController.getBooksByAuthor(author.id());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(bookByAuthor);
    }

    @Test
    void BookController_GetBooksByTag_ReturnsListOfBooks() throws Exception {
        var books = createBooks();
        var tag = testTags.get(rn.nextInt(0, tagsCount - 1));
        var bookByTag = books.stream()
                .filter(book -> book.tags().contains(tag))
                .collect(Collectors.toList());
        doReturn(bookByTag).when(this.bookService).getBooksByTag(tag.id(), new Page(0, booksCount));

        var responseEntity = this.bookController.getBooksByTag(tag.id(), 0, booksCount);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(bookByTag);
    }

    @Test
    void BookController_CreateBook_ReturnsBook() throws Exception {
        var book = createBook(100);
        doReturn(book).when(this.bookService).createBook(book);

        var responseEntity = this.bookController.createBook(book);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isEqualTo(book);
    }

    @Test
    void BookController_UpdateBook_ReturnResult() throws Exception {
        var book = createBook(100);
        doReturn(true).when(this.bookService).updateBook(book);

        var responseEntity = this.bookController.updateBook(book);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isTrue();
    }


    @Test
    void BookController_DeleteBook_ReturnResult() throws Exception {
        var book = createBook(100);
        doReturn(true).when(this.bookService).deleteBook(book.id());

        var responseEntity = this.bookController.deleteBook(book.id());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isTrue();
    }

    private static BookDto createBook(int index) {

        if (testAuthors.isEmpty()) {
            createAuthors();
        }
        if (testTags.isEmpty()) {
            createTags();
        }
        return new BookDto(
                index,
                "Book Title #" + rn.nextInt(),
                testAuthors.get(rn.nextInt(0, authorsCount - 1)).id(),
                1980 + index % 10,
                "ISBN #" + rn.nextInt(0, 9999999),
                Level.values()[rn.nextInt(0, 2)].name(),
                "Description #" + index,
                rn.nextBoolean(),
                testTags
                        .stream()
                        .filter(tag -> rn.nextBoolean())
                        .limit(rn.nextInt(1, 4))
                        .collect(Collectors.toSet())
        );
    }

    private static List<BookDto> createBooks() {

        List<BookDto> books = new ArrayList<>();
        for (int i = 0; i < booksCount; i++) {
            books.add(createBook(i));
        }

        return books;
    }

    private static void createTags() {
        for (int i = 0; i < tagsCount; i++) {
            testTags.add(new TagDto(i, "Tag #" + i));
        }
    }

    private static void createAuthors() {
        for (int i = 0; i < authorsCount; i++) {
            testAuthors.add(new AuthorDto(i, "Author #" + i, "Biography #" + i));
        }
    }
}
