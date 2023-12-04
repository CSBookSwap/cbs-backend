package tech.cbs.api.service;

import tech.cbs.api.service.dto.BookDto;
import tech.cbs.api.service.dto.Page;

import java.util.List;

public interface BookService {

    List<BookDto> getBooks(Page page);

    BookDto getBook(int id);

    BookDto createBook(BookDto bookDto);

    boolean updateBook(BookDto bookDto);

    boolean deleteBook(int id);
}
