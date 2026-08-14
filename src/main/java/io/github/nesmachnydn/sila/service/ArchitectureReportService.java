package io.github.nesmachnydn.sila.service;

import io.github.nesmachnydn.sila.domain.ArchitectureElement;
import io.github.nesmachnydn.sila.domain.ArchitectureRelation;
import io.github.nesmachnydn.sila.domain.ElementType;
import io.github.nesmachnydn.sila.port.SilaRepositoryClient;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Creates a deterministic Markdown architecture snapshot from repository data.
 */
public final class ArchitectureReportService {
    private final SilaRepositoryClient repository;

    public ArchitectureReportService(SilaRepositoryClient repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public String generateMarkdown(String title) {
        String reportTitle = title == null || title.isBlank() ? "Architecture Repository Snapshot" : title.strip();
        List<ArchitectureElement> elements = repository.findElements().stream()
                .sorted(Comparator.comparing(ArchitectureElement::type).thenComparing(ArchitectureElement::name))
                .toList();
        List<ArchitectureRelation> relations = repository.findRelations().stream()
                .sorted(Comparator.comparing(ArchitectureRelation::type).thenComparing(ArchitectureRelation::id))
                .toList();

        Map<ElementType, Long> counts = new EnumMap<>(ElementType.class);
        elements.forEach(element -> counts.merge(element.type(), 1L, Long::sum));
        Map<String, ArchitectureElement> byKey = elements.stream()
                .collect(Collectors.toMap(ArchitectureElement::externalKey, Function.identity()));

        StringBuilder markdown = new StringBuilder();
        markdown.append("# ").append(escape(reportTitle)).append("\n\n");
        markdown.append("Generated from the in-memory repository stub. No external system is contacted.\n\n");

        markdown.append("## Inventory\n\n");
        markdown.append("| Element type | Count |\n|---|---:|\n");
        counts.forEach((type, count) -> markdown.append("| ")
                .append(type.name())
                .append(" | ")
                .append(count)
                .append(" |\n"));
        markdown.append("\n");

        markdown.append("## Application components\n\n");
        List<ArchitectureElement> applications = elements.stream()
                .filter(element -> element.type() == ElementType.APPLICATION_COMPONENT)
                .toList();
        if (applications.isEmpty()) {
            markdown.append("No application components found.\n\n");
        } else {
            markdown.append("| Name | External key |\n|---|---|\n");
            applications.forEach(element -> markdown.append("| ")
                    .append(escape(element.name()))
                    .append(" | `")
                    .append(escape(element.externalKey()))
                    .append("` |\n"));
            markdown.append("\n");
        }

        markdown.append("## Relations\n\n");
        if (relations.isEmpty()) {
            markdown.append("No relations found.\n");
        } else {
            markdown.append("| Type | Source | Target | Description |\n|---|---|---|---|\n");
            for (ArchitectureRelation relation : relations) {
                String sourceName = displayName(byKey, relation.sourceExternalKey());
                String targetName = displayName(byKey, relation.targetExternalKey());
                markdown.append("| ")
                        .append(escape(relation.type()))
                        .append(" | ")
                        .append(escape(sourceName))
                        .append(" | ")
                        .append(escape(targetName))
                        .append(" | ")
                        .append(escape(relation.description()))
                        .append(" |\n");
            }
        }

        return markdown.toString();
    }

    private static String displayName(Map<String, ArchitectureElement> byKey, String key) {
        ArchitectureElement element = byKey.get(key);
        return element == null ? key : element.name();
    }

    private static String escape(String value) {
        return value.replace("|", "\\|").replace("\n", " ");
    }
}
