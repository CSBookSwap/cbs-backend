package tech.cbs.api.repository.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import tech.cbs.api.repository.TagRepository;
import tech.cbs.api.repository.model.Tag;
import tech.cbs.api.repository.rowMapper.TagRowMapper;
import tech.cbs.api.service.dto.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public Tag save(Tag tag) {
        var INSERT_TAG_SQL = "INSERT INTO tag(name) VALUES (:name) RETURNING id;";
        int tagId = parameterJdbcTemplate.queryForObject(INSERT_TAG_SQL, tag.createParameterSources(), Integer.class);
        return findById(tagId).get();
    }

    @Override
    public boolean saveAll(List<Tag> tags) {
        var INSERT_TAG_SQL = "INSERT INTO tag(name) VALUES (:name) RETURNING id;";
        SqlParameterSource[] parameterSources = tags.stream()
                .map(Tag::createParameterSources)
                .toArray(SqlParameterSource[]::new);
        parameterJdbcTemplate.batchUpdate(INSERT_TAG_SQL, parameterSources);
        return true;
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
    public boolean deleteByIds(List<Integer> ids) {
        SqlParameterSource[] parameterSources = ids.stream()
                .map(id -> new MapSqlParameterSource().addValue("id", id))
                .toArray(SqlParameterSource[]::new);
        parameterJdbcTemplate.batchUpdate("DELETE FROM tag WHERE id=:id;", parameterSources);
        return true;
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
