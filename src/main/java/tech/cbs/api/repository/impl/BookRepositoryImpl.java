package tech.cbs.api.repository.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.cbs.api.repository.BookRepository;
import tech.cbs.api.repository.model.Book;
import tech.cbs.api.repository.model.Level;
import tech.cbs.api.repository.model.Tag;
import tech.cbs.api.repository.rowMapper.BookRowMapper;
import tech.cbs.api.service.dto.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    @Transactional(readOnly = true)
    public List<Book> findAll(Page page) {
        var sql = """
                SELECT b.id, b.title, b.author_id, b.publication_year, b.isbn, b.level, b.description, b.available,
                t.id AS tag_id, t.name AS tag_name
                FROM book AS b
                JOIN book_tags bt on b.id = bt.book_id
                LEFT JOIN tag t on t.id = bt.tag_id
                WHERE b.id IN (SELECT id FROM book LIMIT :size OFFSET :offset)
                ORDER BY b.id, t.id;
                """;

        return parameterJdbcTemplate.query(sql, Map.of("offset", page.offset(), "size", page.size()),
                rs -> {
                    Map<Integer, Book> bookMap = new HashMap<>();
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        Book book = bookMap.get(id);
                        if (book == null) {
                            book = new Book(
                                    id,
                                    rs.getString("title"),
                                    rs.getInt("author_id"),
                                    rs.getInt("publication_year"),
                                    rs.getString("isbn"),
                                    Level.valueOf(rs.getString("level")),
                                    rs.getString("description"),
                                    rs.getBoolean("available"),
                                    new HashSet<>()
                            );
                            bookMap.put(id, book);
                        }
                        int tagId = rs.getInt("tag_id");
                        if (tagId != 0) {
                            book.tags().add(
                                    new Tag(
                                            tagId,
                                            rs.getString("tag_name")
                                    ));
                        }
                    }
                    return new ArrayList<>(bookMap.values());
                });
    }

    @Override
    public Optional<Book> findById(int id) {
        var sql = """
                SELECT b.id, b.title, b.author_id, b.publication_year, b.isbn, b.level, b.description, b.available,
                t.id AS tag_id, t.name AS tag_name
                FROM book AS b
                LEFT JOIN book_tags AS bt on b.id = bt.book_id
                LEFT JOIN tag AS t on bt.tag_id = t.id
                WHERE b.id=:id
                LIMIT 1;
                """;
        return parameterJdbcTemplate.query(sql, Map.of("id", id), new BookRowMapper())
                .stream().findFirst();
    }

    @Override
    @Transactional
    public Book save(Book book) {

        var INSERT_BOOK_AND_RETURN_ID_SQL = """
                INSERT INTO book(title, author_id, publication_year, isbn, level, description, available)
                VALUES(:title, :author_id, :publication_year, :isbn, :level, :description, :available)
                RETURNING id;
                """;

        int bookId = parameterJdbcTemplate.queryForObject(INSERT_BOOK_AND_RETURN_ID_SQL, book.createParameterSources(), Integer.class);

        var BATCH_TAG_INSERT_SQL = """
                INSERT INTO book_tags(book_id, tag_id)
                VALUES (:book_id, :tag_id);
                """;
        SqlParameterSource[] batchArgs = book.tags()
                .stream()
                .map(Tag::id)
                .map(tagId -> new MapSqlParameterSource()
                        .addValue("book_id", bookId)
                        .addValue("tag_id", tagId))
                .toArray(SqlParameterSource[]::new);

        parameterJdbcTemplate.batchUpdate(BATCH_TAG_INSERT_SQL, batchArgs);

        return new Book(
                bookId,
                book.title(),
                book.authorId(),
                book.publicationYear(),
                book.isbn(),
                book.level(),
                book.description(),
                book.available(),
                book.tags()
        );
    }

    @Override
    public boolean saveAll(List<Book> books) {
//        SqlParameterSource[] parameterSources =
//                books.stream()
//                        .map(Book::createParameterSources)
//                        .collect(Collectors.toList())
//                .toArray(SqlParameterSource[]::new);
//        parameterJdbcTemplate.batchUpdate(INSERT_BOOK_SQL, parameterSources);
        return true;
    }

    @Override
    @Transactional
    public boolean update(Book book) {

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

        parameterJdbcTemplate.update(sql, book.createParameterSources());

        List<Integer> newTags = new ArrayList<>(book.tags().stream().map(Tag::id).toList());

        List<Integer> tagsInDb = parameterJdbcTemplate
                .queryForList("SELECT tag_id FROM book_tags WHERE book_id = :book_id",
                        new MapSqlParameterSource("book_id", book.id()), Integer.class);

        newTags.removeAll(tagsInDb);

        SqlParameterSource[] argsForBatchDelete = tagsInDb.stream()
                .filter(tagIdInDb -> !newTags.contains(tagIdInDb))
                .map(tagId -> new MapSqlParameterSource()
                        .addValue("book_id", book.id())
                        .addValue("tag_id", tagId))
                .toList()
                .toArray(SqlParameterSource[]::new);

        SqlParameterSource[] argsForBatchAdd = newTags.stream()
                .filter(newTagId -> !tagsInDb.contains(newTagId))
                .map(tagId -> new MapSqlParameterSource()
                        .addValue("book_id", book.id())
                        .addValue("tag_id", tagId))
                .toList()
                .toArray(SqlParameterSource[]::new);

        var BATCH_TAG_DELETE_SQL = "DELETE FROM book_tags WHERE book_id = :book_id AND tag_id = :tag_id;";
        var BATCH_TAG_ADD_SQL = "INSERT INTO book_tags(book_id, tag_id) VALUES (:book_id, :tag_id);";

        parameterJdbcTemplate.batchUpdate(BATCH_TAG_DELETE_SQL, argsForBatchDelete);
        parameterJdbcTemplate.batchUpdate(BATCH_TAG_ADD_SQL, argsForBatchAdd);


        return true;
//        var BATCH_TAG_INSERT_SQL = """
//                INSERT INTO book_tags(book_id, tag_id)
//                VALUES (:book_id, :tag_id);
//                """;
//
//        SqlParameterSource[] batchArgs = book.tags()
//                .stream()
//                .map(tag -> new MapSqlParameterSource()
//                        .addValue("book_id", book.id())
//                        .addValue("tag_id", tag.id()))
//                .collect(Collectors.toList())
//                .toArray(SqlParameterSource[]::new);
//
//        return Arrays.stream(
//                parameterJdbcTemplate.batchUpdate(BATCH_TAG_INSERT_SQL, batchArgs)
//        ).sum() >= 1;
    }

    @Override
    public boolean deleteById(int id) {
        return parameterJdbcTemplate.update("DELETE FROM book WHERE id=:id;", Map.of("id", id)) >= 1;
    }

    @Override
    public boolean deleteByIds(List<Integer> ids) {
        SqlParameterSource[] parameterSources = ids.stream()
                .map(id -> new MapSqlParameterSource().addValue("id", id))
                .toArray(SqlParameterSource[]::new);
        parameterJdbcTemplate.batchUpdate("DELETE FROM book WHERE id=:id;", parameterSources);
        return true;
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

    //todo: rewrite SQL
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

//    private MapSqlParameterSource createParameterSources(Book book) {
//        return new MapSqlParameterSource()
//                .addValue("id", book.id())
//                .addValue("title", book.title())
//                .addValue("author_id", book.authorId())
//                .addValue("publication_year", book.publicationYear())
//                .addValue("isbn", book.isbn())
//                .addValue("level", book.level().name())
//                .addValue("description", book.description())
//                .addValue("available", book.available())
//                .addValue("tags", book.tags().stream()
//                        .map(Tag::id)
//                        .toArray(Integer[]::new));
//    }

//    @Override
//    @Transactional
//    public Book save(Book book) {
//
////        var INSERT_BOOK_AND_RETURN_ID_SQL = """
////                INSERT INTO book(title, author_id, publication_year, isbn, level, description, available)
////                VALUES(:title, :author_id, :publication_year, :isbn, :level, :description, :available)
////                RETURNING id;
////                """;
////        int bookId = parameterJdbcTemplate.queryForObject(INSERT_BOOK_AND_RETURN_ID_SQL, book.createParameterSources(), Integer.class);
//
//        int bookId = insertBookAndReturnId(book);
////
////        var BATCH_TAG_INSERT_SQL = """
////                INSERT INTO book_tags(book_id, tag_id)
////                VALUES (:book_id, :tag_id);
////                """;
////        SqlParameterSource[] batchArgs = book.tags()
////                .stream()
////                .map(Tag::id)
////                .map(tagId -> new MapSqlParameterSource()
////                        .addValue("book_id", bookId)
////                        .addValue("tag_id", tagId))
////                .collect(Collectors.toList())
////                .toArray(SqlParameterSource[]::new);
//
////        parameterJdbcTemplate.batchUpdate(BATCH_TAG_INSERT_SQL, batchArgs);
//
//        var sql = """
//                SELECT b.id, b.title, b.author_id, b.publication_year, b.isbn, b.level, b.description, b.available,
//                t.id AS tag_id, t.name AS tag_name
//                FROM book AS b
//                JOIN book_tags bt on b.id = bt.book_id
//                LEFT JOIN tag t on t.id = bt.tag_id
//                WHERE b.id=:id
//                LIMIT 1;
//                """;
//        return parameterJdbcTemplate.query(sql, Map.of("id", bookId), new BookRowMapper())
//                .stream().findFirst().get();
//    }
}
