package tech.cbs.api.repository.impl;

import org.springframework.stereotype.Repository;
import tech.cbs.api.repository.AuthorRepository;
import tech.cbs.api.repository.model.Author;
import tech.cbs.api.service.dto.Page;

import java.util.List;
import java.util.Optional;

@Repository
public class AuthorRepositoryImpl implements AuthorRepository {

    @Override
    public List<Author> findAll(Page page) {
        return null;
    }

    @Override
    public Optional<Author> findById(int id) {
        return Optional.empty();
    }

    @Override
    public Author save(Author model) {
        return null;
    }

    @Override
    public boolean saveAll(List<Author> models) {
        return false;
    }

    @Override
    public boolean update(Author model) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }
}
