package io.github.nesmachnydn.sila.stub;

import io.github.nesmachnydn.sila.domain.ArchitectureElement;
import io.github.nesmachnydn.sila.domain.ArchitectureRelation;
import io.github.nesmachnydn.sila.domain.ElementType;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public final class DemoRepositoryFixtures {
    public static final String CUSTOMER_PORTAL = "demo:app:customer-portal";
    public static final String DOCUMENT_SERVICE = "demo:app:document-service";
    public static final String IDENTITY_SERVICE = "demo:app:identity-service";

    private DemoRepositoryFixtures() {
    }

    public static InMemorySilaRepositoryStub populatedRepository() {
        InMemorySilaRepositoryStub repository = new InMemorySilaRepositoryStub();
        repository.upsert(element(CUSTOMER_PORTAL, "Customer Portal", "Customer-facing self-service application"));
        repository.upsert(element(DOCUMENT_SERVICE, "Document Service", "Stores and serves business documents"));
        repository.upsert(element(IDENTITY_SERVICE, "Identity Service", "Provides authentication services"));

        repository.addRelation(new ArchitectureRelation(
                stableId("relation:portal-documents"),
                "uses",
                CUSTOMER_PORTAL,
                DOCUMENT_SERVICE,
                "Reads and writes customer documents"));
        repository.addRelation(new ArchitectureRelation(
                stableId("relation:portal-identity"),
                "authenticates-via",
                CUSTOMER_PORTAL,
                IDENTITY_SERVICE,
                "Delegates user authentication"));
        return repository;
    }

    private static ArchitectureElement element(String key, String name, String purpose) {
        return new ArchitectureElement(
                stableId(key),
                key,
                ElementType.APPLICATION_COMPONENT,
                name,
                Map.of("purpose", purpose));
    }

    private static String stableId(String key) {
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8)).toString();
    }
}
