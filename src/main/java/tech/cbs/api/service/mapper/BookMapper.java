package tech.cbs.api.service.mapper;

import tech.cbs.api.repository.model.Book;
import tech.cbs.api.service.dto.BookDto;

import java.util.function.Function;

/**
 * Mapper for {@link Book}
 */
public class BookMapper implements Function<Book, BookDto> {
    @Override
    public BookDto apply(Book book) {
        return null;
    }

    public BookDto toDto(Book book) {
        return apply(book);
    }

    public Book toModel(BookDto bookDto) {
        return null;
    }
}
