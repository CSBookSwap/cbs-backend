package tech.cbs.api.repository.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import tech.cbs.api.repository.model.Tag;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TagRowMapper implements RowMapper<Tag> {
    @Override
    public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Tag(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
