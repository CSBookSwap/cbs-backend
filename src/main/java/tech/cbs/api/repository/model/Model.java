package tech.cbs.api.repository.model;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Model interface
 */
public interface Model {

    MapSqlParameterSource createParameterSources();
}
