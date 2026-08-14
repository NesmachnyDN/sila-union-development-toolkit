package io.github.nesmachnydn.sila.domain;

public record GlossaryTerm(String term, String definition, String source) {
    public GlossaryTerm {
        term = requireText(term, "term");
        definition = requireText(definition, "definition");
        source = source == null || source.isBlank() ? "portfolio-demo" : source.strip();
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.strip();
    }
}
