package io.github.nesmachnydn.sila.service;

import io.github.nesmachnydn.sila.domain.ArchitectureElement;
import io.github.nesmachnydn.sila.domain.ElementType;
import io.github.nesmachnydn.sila.domain.GlossaryTerm;
import io.github.nesmachnydn.sila.domain.ImportResult;
import io.github.nesmachnydn.sila.domain.UpsertOutcome;
import io.github.nesmachnydn.sila.port.SilaRepositoryClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Demonstrates an idempotent architecture-repository automation use case.
 */
public final class GlossaryImportService {
    private final SilaRepositoryClient repository;

    public GlossaryImportService(SilaRepositoryClient repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public ImportResult importSemicolonSeparated(Reader source) throws IOException {
        Objects.requireNonNull(source, "source");
        int created = 0;
        int updated = 0;
        int unchanged = 0;
        int rejected = 0;
        boolean firstDataRow = true;

        try (BufferedReader reader = new BufferedReader(source)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String stripped = line.strip();
                if (stripped.isEmpty() || stripped.startsWith("#")) {
                    continue;
                }

                List<String> fields;
                try {
                    fields = SemicolonSeparatedValues.parseLine(stripped);
                } catch (IllegalArgumentException malformed) {
                    rejected++;
                    continue;
                }

                if (firstDataRow && isHeader(fields)) {
                    firstDataRow = false;
                    continue;
                }
                firstDataRow = false;

                if (fields.size() < 2 || fields.get(0).isBlank() || fields.get(1).isBlank()) {
                    rejected++;
                    continue;
                }

                GlossaryTerm term = new GlossaryTerm(
                        fields.get(0),
                        fields.get(1),
                        fields.size() >= 3 ? fields.get(2) : "portfolio-demo");

                UpsertOutcome outcome = repository.upsert(toElement(term));
                switch (outcome) {
                    case CREATED -> created++;
                    case UPDATED -> updated++;
                    case UNCHANGED -> unchanged++;
                }
            }
        }

        return new ImportResult(created, updated, unchanged, rejected);
    }

    private static ArchitectureElement toElement(GlossaryTerm term) {
        String externalKey = "glossary:" + normalize(term.term());
        return new ArchitectureElement(
                StableIds.fromExternalKey(externalKey),
                externalKey,
                ElementType.GLOSSARY_TERM,
                term.term(),
                Map.of(
                        "definition", term.definition(),
                        "source", term.source()));
    }

    private static boolean isHeader(List<String> fields) {
        return fields.size() >= 2
                && fields.get(0).equalsIgnoreCase("term")
                && fields.get(1).equalsIgnoreCase("definition");
    }

    private static String normalize(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKC)
                .strip()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}]+", "-")
                .replaceAll("^-+|-+$", "");
        return normalized;
    }
}
