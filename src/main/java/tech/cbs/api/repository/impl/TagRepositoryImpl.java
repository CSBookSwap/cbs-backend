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

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tech.cbs.api.repository.TagRepository;
import tech.cbs.api.repository.mapper.TagRowMapper;
import tech.cbs.api.repository.model.Tag;
import tech.cbs.api.service.dto.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link TagRepository}
 */
@Repository
public class TagRepositoryImpl implements TagRepository {

    private final NamedParameterJdbcTemplate parameterJdbcTemplate;

    public TagRepositoryImpl(NamedParameterJdbcTemplate parameterJdbcTemplate) {
        this.parameterJdbcTemplate = parameterJdbcTemplate;
    }

    @Override
    public List<Tag> findAll(Page page) {
        var SQL = """
                SELECT id, name
                FROM tag
                LIMIT :size
                OFFSET :offset;
                """;
        return parameterJdbcTemplate.query(SQL, Map.of("size", page.size(), "offset", page.offset()), new TagRowMapper());
    }

    @Override
    public Optional<Tag> findById(int id) {
        return parameterJdbcTemplate.query("SELECT id, name FROM tag WHERE id=:id LIMIT 1;", Map.of("id", id), new TagRowMapper())
                .stream().findFirst();
    }

    @Override
    public int save(Tag tag) {
        var INSERT_TAG_SQL = "INSERT INTO tag(name) VALUES (:name) RETURNING id;";
        return parameterJdbcTemplate.queryForObject(INSERT_TAG_SQL, tag.createParameterSources(), Integer.class);
    }

    @Override
    public boolean update(Tag tag) {
        return parameterJdbcTemplate.update("UPDATE tag SET name=:name WHERE id=:id;", tag.createParameterSources()) >= 1;
    }

    @Override
    public boolean deleteById(int id) {
        return parameterJdbcTemplate.update("DELETE FROM tag WHERE id=:id;", Map.of("id", id)) >= 1;
    }

    @Override
    public List<Tag> findTagsByBookId(int bookId) {
        var SQL = """
                SELECT tag.id, tag.name
                FROM tag
                         INNER JOIN book_tags bt on tag.id = bt.tag_id
                WHERE bt.book_id = :bookId;
                """;
        return parameterJdbcTemplate.query(SQL, Map.of("bookId", bookId), new TagRowMapper());
    }
}
