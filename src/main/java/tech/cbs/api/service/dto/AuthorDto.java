package tech.cbs.api.service.dto;

public record AuthorDto(
        long id,
        String firstname,
        String lastname,
        String description
) {
}
