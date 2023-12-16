package tech.cbs.api.repository.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import tech.cbs.api.repository.AuthorRepository;
import tech.cbs.api.repository.model.Author;
import tech.cbs.api.repository.rowMapper.AuthorRowMapper;
import tech.cbs.api.service.dto.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public Author save(Author author) {
        var INSERT_AUTHOR_SQL = """
                INSERT INTO author (name, biography)
                VALUES (:name, :biography) RETURNING id;
                """;
        int authorId = parameterJdbcTemplate.queryForObject(INSERT_AUTHOR_SQL, author.createParameterSources(), Integer.class);

        return findById(authorId).get();
    }

    @Override
    public boolean saveAll(List<Author> authors) {
        var INSERT_AUTHOR_SQL = """
                INSERT INTO author (name, biography)
                VALUES (:name, :biography) RETURNING id;
                """;
        SqlParameterSource[] parameterSources = authors.stream()
                .map(Author::createParameterSources)
                .toArray(SqlParameterSource[]::new);
        parameterJdbcTemplate.batchUpdate(INSERT_AUTHOR_SQL, parameterSources);
        return true;
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
        ) >= 1;
    }

    @Override
    public boolean deleteById(int id) {
        return parameterJdbcTemplate.update("DELETE FROM author WHERE id=:id;", Map.of("id", id)) >= 1;
    }

    @Override
    public boolean deleteByIds(List<Integer> ids) {
        SqlParameterSource[] parameterSources = ids.stream()
                .map(id -> new MapSqlParameterSource().addValue("id", id))
                .toArray(SqlParameterSource[]::new);
        parameterJdbcTemplate.batchUpdate("DELETE FROM author WHERE id=:id;", parameterSources);
        return true;
    }
}
