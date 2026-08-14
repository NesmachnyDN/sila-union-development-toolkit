package io.github.nesmachnydn.sila.domain;

public record ArchitectureRelation(
        String id,
        String type,
        String sourceExternalKey,
        String targetExternalKey,
        String description) {

    public ArchitectureRelation {
        id = requireText(id, "id");
        type = requireText(type, "type");
        sourceExternalKey = requireText(sourceExternalKey, "sourceExternalKey");
        targetExternalKey = requireText(targetExternalKey, "targetExternalKey");
        description = description == null ? "" : description.strip();
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }
}
