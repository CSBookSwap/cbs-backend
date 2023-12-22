/*
 * MIT License
 *
 * Copyright (c) 2023 Artyom Nefedov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
import tech.cbs.api.repository.BookRepository;
import tech.cbs.api.repository.TagRepository;
import tech.cbs.api.repository.model.Level;
import tech.cbs.api.service.dto.AuthorDto;
import tech.cbs.api.service.dto.BookDto;
import tech.cbs.api.service.dto.Page;
import tech.cbs.api.service.dto.TagDto;
import tech.cbs.api.service.mapper.AuthorMapper;
import tech.cbs.api.service.mapper.BookMapper;
import tech.cbs.api.service.mapper.TagMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for {@link BookService}
 */
@SpringBootTest
@Testcontainers
class BookServiceTest {

    private final static List<AuthorDto> testAuthors = new ArrayList<>();
    private final static List<TagDto> testTags = new ArrayList<>();
    private final static List<BookDto> testBooks = new ArrayList<>();
    private final static int authorCount = 6;
    private final static int tagCount = 10;
    private final static int bookCount = 32;
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0-alpine3.18");
    private final Random rn = new Random();
    @Autowired
    private BookService bookService;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private NamedParameterJdbcTemplate parameterJdbcTemplate;

    @BeforeEach
    void setData() {

        List<AuthorDto> createdAuthors = new ArrayList<>();

        for (int i = 0; i < authorCount; i++) {
            createdAuthors.add(new AuthorDto(0, "Author name #" + rn.nextInt(), "Biography #" + i));
        }
        createdAuthors.stream()
                .map(AuthorMapper::toModel)
                .map(authorRepository::save)
                .map(authorRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(AuthorMapper::toDto)
                .forEach(testAuthors::add);

        List<TagDto> createdTags = new ArrayList<>();

        for (int i = 0; i < tagCount; i++) {
            createdTags.add(new TagDto(0, "Tag name#" + rn.nextInt()));
        }
        createdTags.stream()
                .map(TagMapper::toModel)
                .map(tagRepository::save)
                .map(tagRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(TagMapper::toDto)
                .forEach(testTags::add);

        List<BookDto> createdBooks = new ArrayList<>();

        for (int i = 0; i < bookCount; i++) {

            createdBooks.add(
                    new BookDto(
                            0,
                            "Book Title #" + rn.nextInt(),
                            testAuthors.get(rn.nextInt(0, authorCount - 1)).id(),
                            1980 + i % 10,
                            "ISBN #" + rn.nextInt(0, 9999999),
                            Level.values()[rn.nextInt(0, 2)].name(),
                            "Description #" + i,
                            i % 2 == 0,
                            testTags
                                    .stream()
                                    .filter(tag -> rn.nextBoolean())
                                    .limit(rn.nextInt(1, 4))
                                    .collect(Collectors.toSet())
                    )
            );
        }
        createdBooks.stream()
                .map(BookMapper::toModel)
                .map(bookRepository::save)
                .map(bookRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(BookMapper::toDto)
                .forEach(testBooks::add);
    }

    @AfterEach
    void cleanData() {
        parameterJdbcTemplate.update("""
                DELETE FROM book;
                DELETE FROM author;
                DELETE FROM tag;
                """, Map.of());
        testAuthors.clear();
        testTags.clear();
        testBooks.clear();
    }

    @Test
    void BookService_GetBooks_ReturnsListOfBookDto() {

        List<BookDto> bookDtos = bookService.getBooks(new Page(0, bookCount));

        assertThat(bookDtos).isNotNull();
        assertThat(bookDtos.size()).isEqualTo(bookCount);
    }

    @Test
    void BookService_GetBook_ReturnsBootDto() {

        BookDto expectedBookDto = testBooks.get(rn.nextInt(0, bookCount - 1));

        BookDto returnedBookDto = bookService.getBook(expectedBookDto.id());

        assertThat(returnedBookDto).isNotNull();
        assertThat(returnedBookDto.id()).isEqualTo(expectedBookDto.id());
        assertThat(returnedBookDto.title()).isEqualTo(expectedBookDto.title());
        assertThat(returnedBookDto.authorId()).isEqualTo(expectedBookDto.authorId());
        assertThat(returnedBookDto.publicationYear()).isEqualTo(expectedBookDto.publicationYear());
        assertThat(returnedBookDto.isbn()).isEqualTo(expectedBookDto.isbn());
        assertThat(returnedBookDto.level()).isEqualTo(expectedBookDto.level());
        assertThat(returnedBookDto.description()).isEqualTo(expectedBookDto.description());
        assertThat(returnedBookDto.available()).isEqualTo(expectedBookDto.available());
        assertThat(returnedBookDto.tags()).isEqualTo(expectedBookDto.tags());
    }

    @Test
    void BookService_CreateBook_ReturnSavedBookDto() {

        BookDto expectedBookDto = new BookDto(
                0,
                "Book Title #" + rn.nextInt(),
                testAuthors.get(rn.nextInt(0, authorCount - 1)).id(),
                rn.nextInt(1980, 2023),
                "ISBN #" + rn.nextInt(1000000),
                Level.values()[rn.nextInt(0, 2)].name(),
                "Description #0",
                true,
                testTags
                        .stream()
                        .limit(rn.nextInt(1, 4))
                        .collect(Collectors.toSet())
        );

        BookDto returnedBookDto = bookService.createBook(expectedBookDto);

        assertThat(returnedBookDto).isNotNull();
        assertThat(returnedBookDto.id()).isNotEqualTo(0);
        assertThat(returnedBookDto.title()).isEqualTo(expectedBookDto.title());
        assertThat(returnedBookDto.authorId()).isEqualTo(expectedBookDto.authorId());
        assertThat(returnedBookDto.publicationYear()).isEqualTo(expectedBookDto.publicationYear());
        assertThat(returnedBookDto.isbn()).isEqualTo(expectedBookDto.isbn());
        assertThat(returnedBookDto.level()).isEqualTo(expectedBookDto.level());
        assertThat(returnedBookDto.description()).isEqualTo(expectedBookDto.description());
        assertThat(returnedBookDto.available()).isEqualTo(expectedBookDto.available());
        assertThat(returnedBookDto.tags()).isEqualTo(expectedBookDto.tags());
        assertThat(returnedBookDto.tags().size()).isEqualTo(expectedBookDto.tags().size());
        returnedBookDto.tags().forEach(tagDto -> {
            assertThat(tagDto.id()).isNotEqualTo(0);
            assertThat(tagDto.name()).isNotNull();
        });
    }

    @Test
    void BookService_UpdateBook_ReturnResultAsBoolean() {

        BookDto originalBookDto = testBooks.get(rn.nextInt(0, bookCount - 1));

        BookDto updatedBookDto = new BookDto(
                originalBookDto.id(),
                "Book Title #" + rn.nextInt(),
                testAuthors.get(rn.nextInt(0, authorCount - 1)).id(),
                rn.nextInt(1980, 2023),
                "ISBN #" + rn.nextInt(1000000),
                Level.values()[rn.nextInt(0, 2)].name(),
                "Description #" + originalBookDto.id(),
                originalBookDto.available(),
                testTags
                        .stream()
                        .limit(rn.nextInt(2, 4))
                        .collect(Collectors.toSet())
        );

        boolean result = bookService.updateBook(updatedBookDto);

        assertThat(result).isTrue();

        BookDto returnedBookDto = bookService.getBook(originalBookDto.id());

        assertThat(returnedBookDto).isNotNull();
        assertThat(returnedBookDto.id()).isEqualTo(updatedBookDto.id());
        assertThat(returnedBookDto.title()).isEqualTo(updatedBookDto.title());
        assertThat(returnedBookDto.authorId()).isEqualTo(updatedBookDto.authorId());
        assertThat(returnedBookDto.publicationYear()).isEqualTo(updatedBookDto.publicationYear());
        assertThat(returnedBookDto.isbn()).isEqualTo(updatedBookDto.isbn());
        assertThat(returnedBookDto.level()).isEqualTo(updatedBookDto.level());
        assertThat(returnedBookDto.description()).isEqualTo(updatedBookDto.description());
        assertThat(returnedBookDto.available()).isEqualTo(updatedBookDto.available());
        assertThat(returnedBookDto.tags()).isEqualTo(updatedBookDto.tags());
        assertThat(returnedBookDto.tags().size()).isEqualTo(updatedBookDto.tags().size());
        returnedBookDto.tags().forEach(tagDto -> {
            assertThat(tagDto.id()).isNotEqualTo(0);
            assertThat(tagDto.name()).isNotNull();
        });
    }

    @Test
    void BookService_DeleteBook_ReturnResultAsBoolean() {
        BookDto bookDto = testBooks.get(rn.nextInt(0, bookCount - 1));

        boolean result = bookService.deleteBook(bookDto.id());

        assertThat(result).isTrue();

        Throwable thrown = assertThrows(ResourceNotFoundException.class, () -> bookService.getBook(bookDto.id()));
        assertThat(thrown.getMessage()).isNotNull();
    }

    @Test
    void BookService_GetBooksByAuthor_ReturnsListOfBookDtoWithAuthor() {

        AuthorDto authorDto = testAuthors.get(rn.nextInt(0, authorCount - 1));

        List<BookDto> bookDtos = bookService.getBooksByAuthor(authorDto.id());

        assertThat(bookDtos).isNotNull();
        assertThat(bookDtos.size()).isGreaterThan(0);
        bookDtos.forEach(bookDto -> assertThat(bookDto.authorId()).isEqualTo(authorDto.id()));
    }

    @Test
    void BookService_GetBooksByTag_ReturnsListOfBookDtoWithTag() {
        int testTagId = testBooks
                .stream()
                .flatMap(bookDto -> bookDto.tags().stream())
                .collect(Collectors.groupingBy(TagDto::id, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= 2)
                .map(Map.Entry::getKey)
                .filter(tagId -> rn.nextBoolean())
                .findAny()
                .orElseGet(() -> testTags.get(rn.nextInt(0, tagCount - 1)).id());


        List<BookDto> bookDtos = bookService.getBooksByTag(testTagId, new Page(0, bookCount));

        assertThat(bookDtos).isNotNull();
        assertThat(bookDtos.size()).isGreaterThan(0);
        bookDtos.forEach(bookDto -> {
            assertThat(bookDto.tags()).isNotNull();
            assertThat(bookDto.tags().size()).isGreaterThan(0);
            assertThat(bookDto.tags().stream().anyMatch(tag -> tag.id() == testTagId)).isTrue();
        });
    }
}