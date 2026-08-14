package io.github.nesmachnydn.sila.domain;

public record ImportResult(int created, int updated, int unchanged, int rejected) {
    public int processed() {
        return created + updated + unchanged + rejected;
    }
}
