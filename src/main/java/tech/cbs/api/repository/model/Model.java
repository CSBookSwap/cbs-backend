package tech.cbs.api.repository.model;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public interface Model {

    MapSqlParameterSource createParameterSources();
}
