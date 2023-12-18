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
import tech.cbs.api.service.dto.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for {@link AuthorRepository}
 */
@SpringBootTest
@Testcontainers
class AuthorRepositoryTest {

    private static final List<Author> testAuthors = new ArrayList<>();
    private static final int authorCount = 30;
    private static final Random rn = new Random();

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0-alpine3.18");
    @Autowired
    private AuthorRepository authorRepository;

    private static Author createAuthor() {
        return new Author(0, "Test Author fullname #" + rn.nextInt(), "test bio");
    }

    @BeforeEach
    void setData() {
        List<Author> authors = new ArrayList<>();
        for (int i = 0; i <= authorCount; i++) {
            authors.add(createAuthor());
        }
        authors.forEach(authorRepository::save);
        testAuthors.addAll(authorRepository.findAll(new Page(0, authorCount)));
    }

    @AfterEach
    void clearData() {

        authorRepository.findAll(new Page(0, authorCount))
                .parallelStream()
                .map(Author::id)
                .forEach(authorRepository::deleteById);
        testAuthors.clear();
    }

    @Test
    void AuthorRepository_FindAll_ReturnsListOfAuthors() {
        List<Author> authors = authorRepository.findAll(new Page(0, authorCount));
        assertEquals(authorCount, authors.size());
    }

    @Test
    void AuthorRepository_FindById_ReturnsAuthorAsOptional() {
        var author = testAuthors.get(rn.nextInt(testAuthors.size()));
        var authorFromDB = authorRepository.findById(author.id());
        assertTrue(authorFromDB.isPresent());
        assertEquals(author, authorFromDB.get());

    }

    @Test
    void AuthorRepository_Save_ReturnsSavedAuthorId() {
        var author = createAuthor();
        var authorId = authorRepository.save(author);

        assertTrue(authorId > 0);

        Optional<Author> authorFromDB = authorRepository.findById(authorId);
        assertTrue(authorFromDB.isPresent());

        Author savedAuthor = authorFromDB.get();
        assertEquals(author.name(), savedAuthor.name());
        assertEquals(author.biography(), savedAuthor.biography());
    }

    @Test
    void AuthorRepository_Update_ReturnsBoolean() {
        Author author = testAuthors.get(rn.nextInt(testAuthors.size()));

        Author updatedAuthor = new Author(
                author.id(),
                "Updated Fullname",
                "Updated Bio");

        assertTrue(authorRepository.update(updatedAuthor));
    }

    @Test
    void AuthorRepository_DeleteById_ReturnsBoolean() {
        var author = testAuthors.get(rn.nextInt(testAuthors.size()));
        assertTrue(authorRepository.deleteById(author.id()));
    }
}