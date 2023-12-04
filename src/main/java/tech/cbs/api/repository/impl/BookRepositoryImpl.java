package tech.cbs.api.repository.impl;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tech.cbs.api.repository.BookRepository;
import tech.cbs.api.repository.model.Book;
import tech.cbs.api.repository.rowMapper.BookRowMapper;
import tech.cbs.api.service.dto.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class BookRepositoryImpl implements BookRepository {

    private final NamedParameterJdbcTemplate parameterJdbcTemplate;

    public BookRepositoryImpl(NamedParameterJdbcTemplate parameterJdbcTemplate) {
        this.parameterJdbcTemplate = parameterJdbcTemplate;
    }

    @Override
    public List<Book> findAll(Page page) {
        var sql = """
                SELECT b.id, b.title, b.author_id, b.publication_year, b.isbn, b.level, b.description, b.available,
                t.id AS tag_id, t.name AS tag_name
                FROM book AS b
                JOIN book_tags bt on b.id = bt.book_id
                LEFT JOIN tag t on t.id = bt.tag_id
                LIMIT :size OFFSET :offset;
                """;

        return parameterJdbcTemplate.query(sql, Map.of("offset", page.offset(), "size", page.size()), new BookRowMapper());
    }

    @Override
    public Optional<Book> findById(int id) {
        return Optional.empty();
    }

    @Override
    public Book save(Book model) {
        return null;
    }

    @Override
    public boolean saveAll(List<Book> models) {
        return false;
    }

    @Override
    public boolean update(Book model) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }

    @Override
    public List<Book> findByAuthorId(int id) {
        return null;
    }

    @Override
    public List<Book> findByTagId(int id, Page page) {
        return null;
    }

    @Override
    public List<Book> findByIds(List<Integer> ids, Page page) {
        return null;
    }
}
