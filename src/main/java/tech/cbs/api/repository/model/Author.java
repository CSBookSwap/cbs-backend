package tech.cbs.api.repository.model;

public record Author(
        int id,
        String name,
        String biography
) implements Model {
}
