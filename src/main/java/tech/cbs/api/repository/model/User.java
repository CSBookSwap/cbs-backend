package tech.cbs.api.repository.model;

public record User(
        int id,
        String username,
        String email
) implements Model {
}
