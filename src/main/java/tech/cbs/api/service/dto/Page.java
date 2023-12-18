package tech.cbs.api.service.dto;

public record Page(int number, int size) {
    public int offset() {
        return number() * size();
    }
}
