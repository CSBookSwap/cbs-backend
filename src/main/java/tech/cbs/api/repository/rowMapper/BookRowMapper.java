package tech.cbs.api.repository.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import tech.cbs.api.repository.model.Book;
import tech.cbs.api.repository.model.Level;
import tech.cbs.api.repository.model.Tag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class BookRowMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        var book = new Book(
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
        mapTag(rs, book);
        return book;
    }

    private void mapTag(ResultSet rs, Book book) throws SQLException {

        while (rs.next() && rs.getInt("id") == book.id()) {
            book.tags().add(new Tag(
                    rs.getInt("tag_id"),
                    rs.getString("tag_name")
            ));
        }

//        String tagName = rs.getString("tag_name");
//        if (tagName != null) {
//            book.tags().add(new Tag(rs.getInt("tag_id"), tagName));
//        }
    }
}
