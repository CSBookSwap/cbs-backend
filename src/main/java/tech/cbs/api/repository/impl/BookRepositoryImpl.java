/*
 * MIT License
 *
 * Copyright (c) 2023 Artyom Nefedov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package tech.cbs.api.repository.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.cbs.api.repository.BookRepository;
import tech.cbs.api.repository.mapper.BookResultSetExtractor;
import tech.cbs.api.repository.mapper.BookRowMapper;
import tech.cbs.api.repository.model.Book;
import tech.cbs.api.repository.model.Tag;
import tech.cbs.api.service.dto.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link BookRepository}
 */
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
                JOIN book_tags AS bt on b.id = bt.book_id
                LEFT JOIN tag AS t on t.id = bt.tag_id
                WHERE b.id IN (SELECT id FROM book LIMIT :size OFFSET :offset)
                ORDER BY b.title;
                """;

        return parameterJdbcTemplate.query(sql, Map.of("offset", page.offset(), "size", page.size()), new BookResultSetExtractor());
//        return parameterJdbcTemplate.query(sql, Map.of("offset", page.offset(), "size", page.size()),
//                rs -> {
//                    Map<Integer, Book> bookMap = new HashMap<>();
//                    while (rs.next()) {
//                        int id = rs.getInt("id");
//                        Book book = bookMap.get(id);
//                        if (book == null) {
//                            book = new Book(
//                                    id,
//                                    rs.getString("title"),
//                                    rs.getInt("author_id"),
//                                    rs.getInt("publication_year"),
//                                    rs.getString("isbn"),
//                                    Level.valueOf(rs.getString("level")),
//                                    rs.getString("description"),
//                                    rs.getBoolean("available"),
//                                    new HashSet<>()
//                            );
//                            bookMap.put(id, book);
//                        }
//                        int tagId = rs.getInt("tag_id");
//                        if (tagId != 0) {
//                            book.tags().add(
//                                    new Tag(
//                                            tagId,
//                                            rs.getString("tag_name")
//                                    ));
//                        }
//                    }
//                    return new ArrayList<>(bookMap.values());
//                });
    }

    @Override
    public Optional<Book> findById(int id) {
        var sql = """
                SELECT b.id, b.title, b.author_id, b.publication_year, b.isbn, b.level, b.description, b.available,
                t.id AS tag_id, t.name AS tag_name
                FROM book AS b
                JOIN book_tags AS bt on b.id = bt.book_id
                LEFT JOIN tag AS t on bt.tag_id = t.id
                WHERE b.id=:id
                ORDER BY tag_id;
                """;

//        return parameterJdbcTemplate.query(sql, Map.of("id", id), new BookRowMapper())
        return parameterJdbcTemplate.query(sql, Map.of("id", id), new BookResultSetExtractor())
                .stream().findFirst();
    }

    @Override
    @Transactional
    public int save(Book book) {

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

        return bookId;
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

        parameterJdbcTemplate.update("DELETE FROM book_tags WHERE book_id=:id;", Map.of("id", book.id()));

        SqlParameterSource[] argsForBatchInsert = book.tags()
                .stream()
                .map(tag -> new MapSqlParameterSource()
                        .addValue("book_id", book.id())
                        .addValue("tag_id", tag.id()))
                .toArray(SqlParameterSource[]::new);

        var BATCH_TAG_ADD_SQL = "INSERT INTO book_tags(book_id, tag_id) VALUES (:book_id, :tag_id);";

        int[] addUpdates = parameterJdbcTemplate.batchUpdate(BATCH_TAG_ADD_SQL, argsForBatchInsert);

        return addUpdates.length == book.tags().size();
    }

    @Override
    @Transactional
    public boolean deleteById(int id) {
        return parameterJdbcTemplate.update("DELETE FROM book WHERE id=:id;", Map.of("id", id)) >= 1;
    }

    @Override
    @Transactional(readOnly = true)
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
}
