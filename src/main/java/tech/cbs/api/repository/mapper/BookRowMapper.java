package tech.cbs.api.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import tech.cbs.api.repository.model.Book;
import tech.cbs.api.repository.model.Level;
import tech.cbs.api.repository.model.Tag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

/**
 * Row mapper for {@link Book}
 */
public class BookRowMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {

        Book book = new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getInt("author_id"),
                rs.getInt("publication_year"),
                rs.getString("isbn"),
                Level.valueOf(rs.getString("level")),
                rs.getString("description"),
                rs.getBoolean("available"),
                new HashSet<>()
        );

        while (rs.next()) {
            int tagId = rs.getInt("tag_id");
            if (tagId != 0) {
                book.tags().add(new Tag(tagId, rs.getString("tag_name")));
            } else {
                break;
            }
        }


        return book;
    }
}
