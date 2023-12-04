package tech.cbs.api.repository;

import tech.cbs.api.repository.model.Book;
import tech.cbs.api.service.dto.Page;

import java.util.List;

public interface BookRepository extends AbstractModelRepository<Book> {

    List<Book> findByAuthorId(int id);

    List<Book> findByTagId(int id, Page page);

    List<Book> findByIds(List<Integer> ids, Page page);
}
