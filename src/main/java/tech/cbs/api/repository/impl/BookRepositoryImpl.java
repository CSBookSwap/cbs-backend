package tech.cbs.api.repository.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.cbs.api.repository.BookRepository;
import tech.cbs.api.repository.model.Book;
import tech.cbs.api.repository.rowMapper.BookRowMapper;
import tech.cbs.api.service.dto.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
                LIMIT :size
                OFFSET :offset;
                """;

        return parameterJdbcTemplate.query(sql, Map.of("offset", page.offset(), "size", page.size()), new BookRowMapper());
    }

    @Override
    public Optional<Book> findById(int id) {
        var sql = """
                SELECT b.id, b.title, b.author_id, b.publication_year, b.isbn, b.level, b.description, b.available,
                t.id AS tag_id, t.name AS tag_name
                FROM book AS b
                JOIN book_tags bt on b.id = bt.book_id
                LEFT JOIN tag t on t.id = bt.tag_id
                WHERE b.id=:id
                LIMIT 1;
                """;
        return parameterJdbcTemplate.query(sql, Map.of("id", id), new BookRowMapper())
                .stream().findFirst();
    }

    @Override
    @Transactional
    public Book save(Book model) {
        var INSERT_BOOK_SQL = """
                INSERT INTO book(title, author_id, publication_year, isbn, level, description, available)
                VALUES (:title, :author_id, :publication_year, :isbn, :level, :description, :available)
                RETURNING id;
                """;
        int bookId = parameterJdbcTemplate.queryForObject(INSERT_BOOK_SQL, createParameterSources(model), Integer.class);

        var tagParams = model.tags()
                .stream()
                .map(tag -> new MapSqlParameterSource()
                        .addValue("tag_id", tag.id())
                        .addValue("book_id", bookId))
                .collect(Collectors.toList()).toArray(new SqlParameterSource[0]);
        parameterJdbcTemplate.batchUpdate("INSERT INTO book_tags(book_id, tag_id) VALUES (:book_id, :tag_id);", tagParams);

        return this.findById(bookId).get();
    }

    @Override
    public boolean saveAll(List<Book> models) {
        var INSERT_BOOK_SQL = """
                WITH new_book AS (
                INSERT INTO book(title, author_id, publication_year, isbn, level, description, available)
                VALUES (:title, :author_id, :publication_year, :isbn, :level, :description, :available)
                RETURNING id)
                INSERT INTO book_tags (book_id, tag_id)
                VALUES ((SELECT id FROM new_book), :tag_id);
                """;

        SqlParameterSource[] parameterSources = models
                .stream()
                .map(book -> this.createParameterSources(book).addValue("tag_id", book.tags().stream().findAny().get().id()))
                .collect(Collectors.toList()).toArray(new SqlParameterSource[0]);
        parameterJdbcTemplate.batchUpdate(INSERT_BOOK_SQL, parameterSources);

        return true;
    }

    @Override
    @Transactional
    public boolean update(Book model) {

        var sql = """
                UPDATE book
                SET title=:title,
                    author_id=:author_id,
                    publication_year=:publication_year,
                    isbn=:isbn,
                    level=:level,
                    description=:description,
                    available=:available
                WHERE id=:id;
                """;

        parameterJdbcTemplate.update(sql, createParameterSources(model));

        var UPDATE_TAG_SQL = new StringBuffer("""
                DELETE FROM book_tags WHERE book_id=:book_id;
                INSERT INTO book_tags(book_id, tag_id)
                VALUES
                """);
        model.tags().forEach(tag -> UPDATE_TAG_SQL.append(" (" + model.id() +", " + tag.id() + ") "));

        return parameterJdbcTemplate.update(UPDATE_TAG_SQL.toString(), Map.of("book_id", model.id())) >= 1;
    }

    @Override
    public boolean deleteById(int id) {
        return parameterJdbcTemplate.update("DELETE FROM book WHERE id=:id;", Map.of("id", id)) >= 1;
    }

    @Override
    public List<Book> findByAuthorId(int id) {
        var sql = """
                SELECT b.id, b.title, b.author_id, b.publication_year, b.isbn, b.level, b.description, b.available,
                t.id AS tag_id, t.name AS tag_name
                FROM book AS b
                JOIN book_tags bt on b.id = bt.book_id
                LEFT JOIN tag t on t.id = bt.tag_id
                WHERE b.author_id=:author_id;
                """;
        return parameterJdbcTemplate.query(sql, Map.of("author_id", id), new BookRowMapper());
    }

    @Override
    public List<Book> findByTagId(int id, Page page) {
        var sql = """
                SELECT b.id, b.title, b.author_id, b.publication_year, b.isbn, b.level, b.description, b.available,
                t.id AS tag_id, t.name AS tag_name
                FROM tag AS t
                JOIN public.book_tags bt on t.id = bt.tag_id
                JOIN public.book b on b.id = bt.book_id
                WHERE t.id=:tag_id
                LIMIT :size
                OFFSET :offset;
                """;
        return parameterJdbcTemplate.query(sql,
                Map.of("tag_id", id, "size", page.size(), "offset", page.offset()),
                new BookRowMapper());
    }

    @Override
    public List<Book> findByIds(List<Integer> ids, Page page) {
        return null;
    }

    private MapSqlParameterSource createParameterSources(Book model) {
        return new MapSqlParameterSource()
                .addValue("id", model.id())
                .addValue("title", model.title())
                .addValue("author_id", model.authorId())
                .addValue("publication_year", model.publicationYear())
                .addValue("isbn", model.isbn())
                .addValue("level", model.level().name())
                .addValue("description", model.description())
                .addValue("available", model.available());
    }
}
