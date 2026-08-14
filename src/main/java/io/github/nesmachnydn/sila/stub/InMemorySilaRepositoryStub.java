package io.github.nesmachnydn.sila.stub;

import io.github.nesmachnydn.sila.domain.ArchitectureElement;
import io.github.nesmachnydn.sila.domain.ArchitectureRelation;
import io.github.nesmachnydn.sila.domain.UpsertOutcome;
import io.github.nesmachnydn.sila.port.SilaRepositoryClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Deterministic test double for repository-facing automation.
 */
public final class InMemorySilaRepositoryStub implements SilaRepositoryClient {
    private final Map<String, ArchitectureElement> elements = new LinkedHashMap<>();
    private final Map<String, ArchitectureRelation> relations = new LinkedHashMap<>();

    @Override
    public Optional<ArchitectureElement> findByExternalKey(String externalKey) {
        return Optional.ofNullable(elements.get(externalKey));
    }

    @Override
    public List<ArchitectureElement> findElements() {
        return List.copyOf(elements.values());
    }

    @Override
    public List<ArchitectureRelation> findRelations() {
        return List.copyOf(relations.values());
    }

    @Override
    public UpsertOutcome upsert(ArchitectureElement element) {
        ArchitectureElement existing = elements.get(element.externalKey());
        if (existing == null) {
            elements.put(element.externalKey(), element);
            return UpsertOutcome.CREATED;
        }
        if (sameBusinessContent(existing, element)) {
            return UpsertOutcome.UNCHANGED;
        }
        elements.put(element.externalKey(), element);
        return UpsertOutcome.UPDATED;
    }

    @Override
    public void addRelation(ArchitectureRelation relation) {
        requireKnownEndpoint(relation.sourceExternalKey(), "source");
        requireKnownEndpoint(relation.targetExternalKey(), "target");
        relations.put(relation.id(), relation);
    }

    public void clear() {
        elements.clear();
        relations.clear();
    }

    private void requireKnownEndpoint(String externalKey, String role) {
        if (!elements.containsKey(externalKey)) {
            throw new IllegalArgumentException("Unknown " + role + " element: " + externalKey);
        }
    }

    private static boolean sameBusinessContent(ArchitectureElement left, ArchitectureElement right) {
        return left.externalKey().equals(right.externalKey())
                && left.type() == right.type()
                && left.name().equals(right.name())
                && left.attributes().equals(right.attributes());
    }
}
