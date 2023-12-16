package tech.cbs.api.repository.model;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public record Author(
        int id,
        String name,
        String biography
) implements Model {
    @Override
    public MapSqlParameterSource createParameterSources() {
        return new MapSqlParameterSource()
                .addValue("id", this.id())
                .addValue("name", this.name())
                .addValue("biography", this.biography());
    }
}
