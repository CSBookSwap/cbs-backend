package tech.cbs.api.repository.model;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public record Tag(int id, String name) implements Model {
    @Override
    public MapSqlParameterSource createParameterSources() {
        return new MapSqlParameterSource()
                .addValue("id", this.id())
                .addValue("name", this.name());
    }
}
