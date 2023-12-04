package tech.cbs.api.repository.model;

import java.util.Set;

public record Book(
        int id,
        String title,
        int authorId,
        int publicationYear,
        String isbn,
        Level level,
        String description,
        boolean available,
        Set<Tag> tags
) implements Model {
}
