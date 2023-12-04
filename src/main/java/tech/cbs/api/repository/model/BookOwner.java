package tech.cbs.api.repository.model;

public record BookOwner(
        int bookId,
        int ownerId,
        String language,
        String location
) {
}
