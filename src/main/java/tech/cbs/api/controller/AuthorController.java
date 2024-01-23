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

package tech.cbs.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.cbs.api.service.AuthorService;
import tech.cbs.api.service.dto.AuthorDto;
import tech.cbs.api.service.dto.Page;

import java.util.List;

@RestControllerAdvice
@RequestMapping("/api/v1/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/{num}/{size}")
    public ResponseEntity<List<AuthorDto>> getAuthors(@PathVariable("num") int num, @PathVariable("size") int size) {
        return ResponseEntity.ok(authorService.getAuthors(new Page(num, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthor(@PathVariable("id") int id) {
        return ResponseEntity.ok(authorService.getAuthor(id));
    }

    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(AuthorDto authorDto) {
        return new ResponseEntity<>(authorService.createAuthor(authorDto), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Boolean> updateAuthor(AuthorDto authorDto) {
        return ResponseEntity.ok(authorService.updateAuthor(authorDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteAuthor(@PathVariable("id") int id) {
        return ResponseEntity.ok(authorService.deleteAuthor(id));
    }
}
