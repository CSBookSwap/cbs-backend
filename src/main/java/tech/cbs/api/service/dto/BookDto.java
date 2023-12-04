package tech.cbs.api.service.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record BookDto(
        long id,
        String name,
        String description,
        String isbn,
        LocalDate datePublished,
        List<AuthorDto> authors,
        Set<String> languages
) {
}
