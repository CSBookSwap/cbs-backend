package tech.cbs.api.service.impl;

import org.springframework.stereotype.Service;
import tech.cbs.api.repository.AuthorRepository;
import tech.cbs.api.service.AuthorService;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }
}
