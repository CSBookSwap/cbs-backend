package tech.cbs.api.repository.model;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public record User(
        int id,
        String username,
        String email
) implements Model {
    @Override
    public MapSqlParameterSource createParameterSources() {
        return new MapSqlParameterSource()
                .addValue("id", this.id())
                .addValue("username", this.username())
                .addValue("email", this.email());
    }
}
