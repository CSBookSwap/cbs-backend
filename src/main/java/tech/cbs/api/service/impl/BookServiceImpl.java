package tech.cbs.api.service.impl;

import org.springframework.stereotype.Service;
import tech.cbs.api.exception.ResourceNotFoundException;
import tech.cbs.api.repository.BookRepository;
import tech.cbs.api.service.BookService;
import tech.cbs.api.service.dto.BookDto;
import tech.cbs.api.service.dto.Page;
import tech.cbs.api.service.mapper.BookMapper;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<BookDto> getBooks(Page page) {
        return bookRepository.findAll(page)
                .stream()
                .map(BookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto getBook(int id) {
        return bookRepository.findById(id)
                .map(BookMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find book with id " + id));
    }

    @Override
    public BookDto createBook(BookDto bookDto) {

        var book = BookMapper.toModel(bookDto);
        int bookId = bookRepository.save(book);
        return bookRepository.findById(bookId)
                .map(BookMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Book cannot be created."));
    }

    @Override
    public boolean updateBook(BookDto bookDto) {
        return bookRepository.update(BookMapper.toModel(bookDto));
    }

    @Override
    public boolean deleteBook(int id) {
        return bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> getBooksByAuthor(int id) {
        return bookRepository.findByAuthorId(id)
                .stream()
                .map(BookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDto> getBooksByTag(int id, Page page) {
        return bookRepository.findByTagId(id, page)
                .stream()
                .map(BookMapper::toDto)
                .toList();
    }
}
