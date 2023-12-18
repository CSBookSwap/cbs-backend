package tech.cbs.api.repository;

import tech.cbs.api.repository.model.Tag;

import java.util.List;

/**
 * Interface for tag repository
 */
public interface TagRepository extends AbstractModelRepository<Tag> {

    /**
     * Find tags by book id
     *
     * @param bookId book id
     * @return list of tags
     */
    List<Tag> findTagsByBookId(int bookId);
}
