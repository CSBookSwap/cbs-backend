package tech.cbs.api.service.mapper;

import tech.cbs.api.repository.model.Book;
import tech.cbs.api.repository.model.Level;
import tech.cbs.api.repository.model.Tag;
import tech.cbs.api.service.dto.BookDto;
import tech.cbs.api.service.dto.TagDto;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mapper for {@link Book}
 */
public class BookMapper implements Function<Book, BookDto> {
    public static BookDto toDto(Book book) {
        return new BookDto(
                book.id(),
                book.title(),
                book.authorId(),
                book.publicationYear(),
                book.isbn(),
                book.level().name(),
                book.description(),
                book.available(),
                book.tags().stream()
                        .map(tag -> new TagDto(tag.id(), tag.name()))
                        .collect(Collectors.toSet())
        );
    }

    public static Book toModel(BookDto bookDto) {
        return new Book(
                bookDto.id(),
                bookDto.title(),
                bookDto.authorId(),
                bookDto.publicationYear(),
                bookDto.isbn(),
                Level.valueOf(bookDto.level()),
                bookDto.description(),
                bookDto.available(),
                bookDto.tags().stream()
                        .map(tagDto -> new Tag(tagDto.id(), tagDto.name()))
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public BookDto apply(Book book) {
        return new BookDto(
                book.id(),
                book.title(),
                book.authorId(),
                book.publicationYear(),
                book.isbn(),
                book.level().name(),
                book.description(),
                book.available(),
                book.tags().stream()
                        .map(tag -> new TagDto(tag.id(), tag.name()))
                        .collect(Collectors.toSet())
        );
    }
}
