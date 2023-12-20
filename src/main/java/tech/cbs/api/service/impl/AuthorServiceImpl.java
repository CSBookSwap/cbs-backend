package tech.cbs.api.service.impl;

import org.springframework.stereotype.Service;
import tech.cbs.api.exception.ResourceNotFoundException;
import tech.cbs.api.repository.AuthorRepository;
import tech.cbs.api.service.AuthorService;
import tech.cbs.api.service.dto.AuthorDto;
import tech.cbs.api.service.dto.Page;
import tech.cbs.api.service.mapper.AuthorMapper;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public List<AuthorDto> getAuthors(Page page) {
        return authorRepository.findAll(page)
                .stream()
                .map(AuthorMapper::toDto)
                .toList();
    }

    @Override
    public AuthorDto getAuthor(int id) {
        return authorRepository.findById(id)
                .map(AuthorMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id " + id + " not found."));
    }

    @Override
    public AuthorDto createAuthor(AuthorDto authorDto) {

        var author = AuthorMapper.toModel(authorDto);
        var id = authorRepository.save(author);

        return authorRepository.findById(id)
                .map(AuthorMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id " + id + " cannot be created."));
    }

    @Override
    public boolean updateAuthor(AuthorDto authorDto) {
        return authorRepository.update(AuthorMapper.toModel(authorDto));
    }

    @Override
    public boolean deleteAuthor(int id) {
        return authorRepository.deleteById(id);
    }
}
