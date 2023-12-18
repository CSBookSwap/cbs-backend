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

package tech.cbs.api.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tech.cbs.api.repository.model.Author;
import tech.cbs.api.repository.model.Book;
import tech.cbs.api.repository.model.Level;
import tech.cbs.api.repository.model.Tag;
import tech.cbs.api.service.dto.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for {@link TagRepository}
 */
@SpringBootTest
@Testcontainers
class TagRepositoryTest {


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
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private TagRepository tagRepository;

    private static Book createBook() {

        int author = rn.nextInt(testAuthors.size());
        int tagCountForBook = rn.nextInt(4);

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

    private static Author createAuthor() {
        return new Author(0, "Test Author fullname #" + rn.nextInt(), "test bio");
    }

    private static Tag createTag() {
        return new Tag(0, "Test Tag name #" + rn.nextInt(15000));
    }

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

    @AfterEach
    void clearData() {

        authorRepository.findAll(new Page(0, authorCount))
                .parallelStream()
                .map(Author::id)
                .forEach(authorRepository::deleteById);
        testAuthors.clear();

        tagRepository.findAll(new Page(0, tagCount))
                .parallelStream()
                .map(Tag::id)
                .forEach(tagRepository::deleteById);
        testTags.clear();

        bookRepository.findAll(new Page(0, bookCount))
                .parallelStream()
                .map(Book::id)
                .forEach(bookRepository::deleteById);
        testBooks.clear();
    }

    @Test
    void TagRepository_FindAll_ReturnTagList() {
        var tags = tagRepository.findAll(new Page(0, tagCount));
        assertEquals(tagCount, tags.size());
    }

    @Test
    void TagRepository_FindById_ReturnTagAsOptional() {

        int tagNum = rn.nextInt(tagCount);

        var tag = tagRepository.findById(testTags.get(tagNum).id());
        assertTrue(tag.isPresent());
        assertEquals(testTags.get(tagNum), tag.get());
    }

    @Test
    void TagRepository_Save_ReturnTag() {
        var tag = createTag();
        var savedTagId = tagRepository.save(tag);
        Optional<Tag> savedTagOptional = tagRepository.findById(savedTagId);

        assertTrue(savedTagOptional.isPresent());

        Tag savedTag = savedTagOptional.get();

        assertEquals(tag.name(), savedTag.name());
    }

    @Test
    void TagRepository_Update_ReturnBoolean() {
        var tag = testTags.get(rn.nextInt(testTags.size()));
        Tag updatedTag = new Tag(tag.id(), "Updated Tag Name");
        boolean result = tagRepository.update(updatedTag);

        assertTrue(result);
    }

    @Test
    void TagRepository_DeleteById_ReturnBoolean() {
        var tag = testTags.get(rn.nextInt(testTags.size()));
        boolean result = tagRepository.deleteById(tag.id());

        assertTrue(result);
    }

    @Test
    void TagRepository_FindTagsByBookId_ReturnsListOfTags() {
        var book = testBooks.get(rn.nextInt(testBooks.size()));
        var tags = tagRepository.findTagsByBookId(book.id());

        assertEquals(book.tags().size(), tags.size());
    }
}