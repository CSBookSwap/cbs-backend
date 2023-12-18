package tech.cbs.api.service.impl;

import org.springframework.stereotype.Service;
import tech.cbs.api.exception.ResourceNotFoundException;
import tech.cbs.api.repository.BookRepository;
import tech.cbs.api.service.BookService;
import tech.cbs.api.service.dto.BookDto;
import tech.cbs.api.service.dto.Page;
import tech.cbs.api.service.mapper.BookMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.bookMapper = new BookMapper();
    }

    @Override
    public List<BookDto> getBooks(Page page) {
        return bookRepository.findAll(page)
                .stream()
                .map(bookMapper)
                .collect(Collectors.toList());
    }

    @Override
    public BookDto getBook(int id) {
        return bookMapper.toDto(bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find book with id " + id)));
    }

    @Override
    public BookDto createBook(BookDto bookDto) {

        int bookId = bookRepository.save(bookMapper.toModel(bookDto));
        return bookMapper.toDto(bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find book with id " + bookId)));
    }

    @Override
    public boolean updateBook(BookDto bookDto) {
        return bookRepository.update(bookMapper.toModel(bookDto));
    }

    @Override
    public boolean deleteBook(int id) {
        return bookRepository.deleteById(id);
    }
}
