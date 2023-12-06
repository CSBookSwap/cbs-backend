package tech.cbs.api.repository.impl;

import org.springframework.stereotype.Repository;
import tech.cbs.api.repository.TagRepository;
import tech.cbs.api.repository.model.Tag;
import tech.cbs.api.service.dto.Page;

import java.util.List;
import java.util.Optional;

@Repository
public class TagRepositoryImpl implements TagRepository {

    @Override
    public List<Tag> findAll(Page page) {
        return null;
    }

    @Override
    public Optional<Tag> findById(int id) {
        return Optional.empty();
    }

    @Override
    public Tag save(Tag model) {
        return null;
    }

    @Override
    public boolean saveAll(List<Tag> models) {
        return false;
    }

    @Override
    public boolean update(Tag model) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }
}
