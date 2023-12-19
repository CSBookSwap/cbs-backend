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

package tech.cbs.api.service.mapper;

import org.junit.jupiter.api.Test;
import tech.cbs.api.repository.model.Author;
import tech.cbs.api.repository.model.Book;
import tech.cbs.api.repository.model.Level;
import tech.cbs.api.repository.model.Tag;
import tech.cbs.api.service.dto.AuthorDto;
import tech.cbs.api.service.dto.BookDto;
import tech.cbs.api.service.dto.TagDto;

import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link BookMapper}, {@link AuthorMapper} and {@link TagMapper}
 */
class ModelDtoMapperTest {

    private final Random rn = new Random();

    @Test
    void BookMapper_ToDto_ReturnsBookDto() {
        Book bookModel = new Book(
                rn.nextInt(1, 1000),
                "Test Book Title",
                rn.nextInt(1, 1000),
                rn.nextInt(1982, 2023),
                "978-3-16-148410-0",
                Level.values()[rn.nextInt(0, 2)],
                "Test Book Description",
                rn.nextBoolean(),
                Set.of(
                        new Tag(rn.nextInt(1, 1000), "Test Tag Name #" + rn.nextInt()),
                        new Tag(rn.nextInt(1, 1000), "Test Tag Name #" + rn.nextInt()),
                        new Tag(rn.nextInt(1, 1000), "Test Tag Name #" + rn.nextInt())
                )
        );

        BookDto bookDto = BookMapper.toDto(bookModel);

        assertThat(bookDto).isNotNull();
        assertThat(bookDto.id()).isEqualTo(bookModel.id());
        assertThat(bookDto.title()).isEqualTo(bookModel.title());
        assertThat(bookDto.authorId()).isEqualTo(bookModel.authorId());
        assertThat(bookDto.publicationYear()).isEqualTo(bookModel.publicationYear());
        assertThat(bookDto.isbn()).isEqualTo(bookModel.isbn());
        assertThat(bookDto.level()).isEqualTo(bookModel.level().name());
        assertThat(bookDto.description()).isEqualTo(bookModel.description());
        assertThat(bookDto.available()).isEqualTo(bookModel.available());
        assertThat(bookDto.tags()).isNotNull();
        assertThat(bookDto.tags().size()).isEqualTo(bookModel.tags().size());
        bookDto.tags().forEach(tagDto -> {
            assertThat(tagDto.id()).isNotEqualTo(0);
            assertThat(tagDto.name()).isNotNull();
        });
    }

    @Test
    void BookMapper_ToModel_ReturnsBook() {
        BookDto bookDto = new BookDto(
                rn.nextInt(1, 1000),
                "Test Book Title",
                rn.nextInt(1, 1000),
                rn.nextInt(1982, 2023),
                "978-3-16-148410-0",
                Level.values()[rn.nextInt(0, 2)].name(),
                "Test Book Description",
                rn.nextBoolean(),
                Set.of(
                        new TagDto(rn.nextInt(1, 1000), "Test Tag Name #" + rn.nextInt()),
                        new TagDto(rn.nextInt(1, 1000), "Test Tag Name #" + rn.nextInt()),
                        new TagDto(rn.nextInt(1, 1000), "Test Tag Name #" + rn.nextInt())
                )
        );

        Book bookModel = BookMapper.toModel(bookDto);

        assertThat(bookModel).isNotNull();
        assertThat(bookModel.id()).isEqualTo(bookDto.id());
        assertThat(bookModel.title()).isEqualTo(bookDto.title());
        assertThat(bookModel.authorId()).isEqualTo(bookDto.authorId());
        assertThat(bookModel.publicationYear()).isEqualTo(bookDto.publicationYear());
        assertThat(bookModel.isbn()).isEqualTo(bookDto.isbn());
        assertThat(bookModel.level()).isEqualTo(Level.valueOf(bookDto.level()));
        assertThat(bookModel.description()).isEqualTo(bookDto.description());
        assertThat(bookModel.available()).isEqualTo(bookDto.available());
        assertThat(bookModel.tags()).isNotNull();
        assertThat(bookModel.tags().size()).isEqualTo(bookDto.tags().size());
        bookModel.tags().forEach(tag -> {
            assertThat(tag.id()).isNotEqualTo(0);
            assertThat(tag.name()).isNotNull();
        });
    }

    @Test
    void AuthorMapper_ToDto_ReturnsAuthorDto() {
        Author author = new Author(
                rn.nextInt(1, 1000),
                "Test Author full name",
                "Test Author biography"
        );

        AuthorDto authorDto = AuthorMapper.toDto(author);

        assertThat(authorDto).isNotNull();
        assertThat(authorDto.id()).isEqualTo(author.id());
        assertThat(authorDto.name()).isEqualTo(author.name());
        assertThat(authorDto.biography()).isEqualTo(author.biography());
    }

    @Test
    void AuthorMapper_ToModel_ReturnAuthor() {

    }

    @Test
    void TagMapper_ToDto_ReturnsTagDto() {
    }

    @Test
    void TagMapper_ToModel_ReturnTag() {
    }
}