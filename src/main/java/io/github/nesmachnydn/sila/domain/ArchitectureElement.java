package io.github.nesmachnydn.sila.domain;

import java.util.Map;
import java.util.Objects;

public record ArchitectureElement(
        String id,
        String externalKey,
        ElementType type,
        String name,
        Map<String, String> attributes) {

    public ArchitectureElement {
        id = requireText(id, "id");
        externalKey = requireText(externalKey, "externalKey");
        type = Objects.requireNonNull(type, "type");
        name = requireText(name, "name");
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }
}
