package tech.cbs.api.service.dto;

import java.util.Set;

public record BookDto(
        int id,
        String title,
        int authorId,
        int publicationYear,
        String isbn,
        String level,
        String description,
        boolean available,
        Set<TagDto> tags
) {
}
