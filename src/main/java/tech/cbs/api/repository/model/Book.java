package tech.cbs.api.repository.model;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.Types;
import java.util.Set;

/**
 * Book model
 */
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

    @Override
    public MapSqlParameterSource createParameterSources() {
        return new MapSqlParameterSource()
                .addValue("id", this.id())
                .addValue("title", this.title())
                .addValue("author_id", this.authorId())
                .addValue("publication_year", this.publicationYear())
                .addValue("isbn", this.isbn())
                .addValue("level", this.level().name())
                .addValue("description", this.description())
                .addValue("available", this.available())
                .addValue("tags", this.tags().stream()
                        .map(Tag::id)
                        .mapToInt(i -> i)
                        .toArray(), Types.ARRAY);
    }
}
