package tech.cbs.api.repository.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import tech.cbs.api.repository.model.Author;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorRowMapper implements RowMapper<Author> {
    @Override
    public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Author(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("biography")
        );
    }
}
