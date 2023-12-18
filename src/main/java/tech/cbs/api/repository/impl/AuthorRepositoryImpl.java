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
import tech.cbs.api.repository.AuthorRepository;
import tech.cbs.api.repository.mapper.AuthorRowMapper;
import tech.cbs.api.repository.model.Author;
import tech.cbs.api.service.dto.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link AuthorRepository}
 */
@Repository
public class AuthorRepositoryImpl implements AuthorRepository {

    private final NamedParameterJdbcTemplate parameterJdbcTemplate;

    public AuthorRepositoryImpl(NamedParameterJdbcTemplate parameterJdbcTemplate) {
        this.parameterJdbcTemplate = parameterJdbcTemplate;
    }

    @Override
    public List<Author> findAll(Page page) {
        var SQL = """
                SELECT id, name, biography
                FROM author
                LIMIT :size
                OFFSET :offset;
                """;
        return parameterJdbcTemplate.query(SQL, Map.of("size", page.size(), "offset", page.offset()), new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(int id) {
        return parameterJdbcTemplate.query(
                "SELECT id, name, biography FROM author WHERE id=:id LIMIT 1;",
                Map.of("id", id),
                new AuthorRowMapper()
        ).stream().findFirst();
    }

    @Override
    public int save(Author author) {
        var INSERT_AUTHOR_SQL = """
                INSERT INTO author (name, biography)
                VALUES (:name, :biography)
                RETURNING id;
                """;
        return parameterJdbcTemplate.queryForObject(INSERT_AUTHOR_SQL, author.createParameterSources(), Integer.class);
    }

    @Override
    public boolean update(Author author) {
        return parameterJdbcTemplate.update("""
                        UPDATE author
                        SET name=:name,
                            biography=:biography
                        WHERE id=:id;
                        """,
                author.createParameterSources()
        ) == 1;
    }

    @Override
    public boolean deleteById(int id) {
        return parameterJdbcTemplate.update("DELETE FROM author WHERE id=:id;", Map.of("id", id)) >= 1;
    }
}
