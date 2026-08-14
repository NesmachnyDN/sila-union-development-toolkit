package io.github.nesmachnydn.sila.stub;

import io.github.nesmachnydn.sila.domain.ArchitectureElement;
import io.github.nesmachnydn.sila.domain.ArchitectureRelation;
import io.github.nesmachnydn.sila.domain.ElementType;
import io.github.nesmachnydn.sila.domain.UpsertOutcome;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemorySilaRepositoryStubTest {

    @Test
    void createsUpdatesAndDetectsUnchangedElements() {
        InMemorySilaRepositoryStub repository = new InMemorySilaRepositoryStub();
        ArchitectureElement first = element("one", "First");
        ArchitectureElement changed = element("one", "Changed");

        assertEquals(UpsertOutcome.CREATED, repository.upsert(first));
        assertEquals(UpsertOutcome.UNCHANGED, repository.upsert(first));
        assertEquals(UpsertOutcome.UPDATED, repository.upsert(changed));
        assertEquals("Changed", repository.findByExternalKey("one").orElseThrow().name());
    }

    @Test
    void relationRequiresKnownEndpoints() {
        InMemorySilaRepositoryStub repository = new InMemorySilaRepositoryStub();
        repository.upsert(element("one", "First"));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () ->
                repository.addRelation(new ArchitectureRelation("r1", "uses", "one", "missing", "")));

        assertTrue(error.getMessage().contains("Unknown target"));
    }

    @Test
    void returnsDefensiveSnapshots() {
        InMemorySilaRepositoryStub repository = new InMemorySilaRepositoryStub();
        repository.upsert(element("one", "First"));

        var snapshot = repository.findElements();
        repository.clear();

        assertEquals(1, snapshot.size());
        assertTrue(repository.findElements().isEmpty());
    }

    private static ArchitectureElement element(String key, String name) {
        return new ArchitectureElement("id-" + key, key, ElementType.APPLICATION_COMPONENT, name, Map.of());
    }
}
