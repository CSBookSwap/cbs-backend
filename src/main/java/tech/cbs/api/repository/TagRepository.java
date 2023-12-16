package tech.cbs.api.repository;

import tech.cbs.api.repository.model.Tag;

import java.util.List;

public interface TagRepository extends AbstractModelRepository<Tag> {

    List<Tag> findTagsByBookId(int bookId);
}
