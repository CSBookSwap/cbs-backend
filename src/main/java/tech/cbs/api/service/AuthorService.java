package tech.cbs.api.service;

import tech.cbs.api.service.dto.AuthorDto;
import tech.cbs.api.service.dto.Page;

import java.util.List;

public interface AuthorService {
    List<AuthorDto> getAuthors(Page page);

    AuthorDto getAuthor(int id);

    AuthorDto createAuthor(AuthorDto authorDto);

    boolean updateAuthor(AuthorDto authorDto);

    boolean deleteAuthor(int id);
}
