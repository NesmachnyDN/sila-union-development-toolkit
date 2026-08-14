package io.github.nesmachnydn.sila.port;

import io.github.nesmachnydn.sila.domain.ArchitectureElement;
import io.github.nesmachnydn.sila.domain.ArchitectureRelation;
import io.github.nesmachnydn.sila.domain.UpsertOutcome;

import java.util.List;
import java.util.Optional;

/**
 * Application port for repository operations needed by the examples.
 *
 * <p>The public portfolio project deliberately contains no real SILA Union network adapter.
 * Tests and the demo use an in-memory stub so the build is deterministic and requires no
 * vendor installation, credentials, endpoint, or proprietary client library.</p>
 */
public interface SilaRepositoryClient {
    Optional<ArchitectureElement> findByExternalKey(String externalKey);

    List<ArchitectureElement> findElements();

    List<ArchitectureRelation> findRelations();

    UpsertOutcome upsert(ArchitectureElement element);

    void addRelation(ArchitectureRelation relation);
}
