package tech.cbs.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.cbs.api.service.dto.BookDto;
import tech.cbs.api.service.dto.Page;
import tech.cbs.api.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getBooks(@RequestBody Page page) {
        return ResponseEntity.ok(bookService.getBooks(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBook(@PathVariable("id") int id) {
        return ResponseEntity.ok(bookService.getBook(id));
    }

    @PostMapping
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto bookDto) {
        return new ResponseEntity<>(bookService.createBook(bookDto), HttpStatus.CREATED);
    }
    
    @PutMapping
    public ResponseEntity<Boolean> updateBook(@RequestBody BookDto bookDto) {
        return ResponseEntity.ok(bookService.updateBook(bookDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteBook(@PathVariable("id") int id) {
        return ResponseEntity.ok(bookService.deleteBook(id));
    }
}
