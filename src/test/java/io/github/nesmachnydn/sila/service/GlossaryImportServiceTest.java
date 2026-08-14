package io.github.nesmachnydn.sila.service;

import io.github.nesmachnydn.sila.domain.ElementType;
import io.github.nesmachnydn.sila.stub.InMemorySilaRepositoryStub;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlossaryImportServiceTest {

    @Test
    void importsValidRowsAndSkipsHeaderAndComments() throws Exception {
        InMemorySilaRepositoryStub repository = new InMemorySilaRepositoryStub();
        GlossaryImportService service = new GlossaryImportService(repository);
        String input = """
                # synthetic data
                term;definition;source
                Capability;An ability to achieve an outcome;Demo
                Application;A software system;Demo
                """;

        var result = service.importSemicolonSeparated(new StringReader(input));

        assertEquals(2, result.created());
        assertEquals(0, result.rejected());
        assertEquals(2, result.processed());
        assertEquals(2, repository.findElements().stream()
                .filter(element -> element.type() == ElementType.GLOSSARY_TERM)
                .count());
    }

    @Test
    void secondIdenticalImportIsIdempotent() throws Exception {
        InMemorySilaRepositoryStub repository = new InMemorySilaRepositoryStub();
        GlossaryImportService service = new GlossaryImportService(repository);
        String input = "term;definition;source\nCapability;An ability;Demo\n";

        var first = service.importSemicolonSeparated(new StringReader(input));
        var second = service.importSemicolonSeparated(new StringReader(input));

        assertEquals(1, first.created());
        assertEquals(1, second.unchanged());
        assertEquals(1, repository.findElements().size());
    }

    @Test
    void updatesDefinitionForSameNormalizedTerm() throws Exception {
        InMemorySilaRepositoryStub repository = new InMemorySilaRepositoryStub();
        GlossaryImportService service = new GlossaryImportService(repository);

        service.importSemicolonSeparated(new StringReader("Capability;Old definition;Demo\n"));
        var update = service.importSemicolonSeparated(new StringReader("  CAPABILITY  ;New definition;Demo\n"));

        assertEquals(1, update.updated());
        var element = repository.findByExternalKey("glossary:capability").orElseThrow();
        assertEquals("New definition", element.attributes().get("definition"));
    }

    @Test
    void supportsQuotedSemicolonsAndEscapedQuotes() throws Exception {
        InMemorySilaRepositoryStub repository = new InMemorySilaRepositoryStub();
        GlossaryImportService service = new GlossaryImportService(repository);
        String input = "\"Decision; record\";\"Text with ; and \"\"quotes\"\"\";Demo\n";

        var result = service.importSemicolonSeparated(new StringReader(input));

        assertEquals(1, result.created());
        var element = repository.findByExternalKey("glossary:decision-record").orElseThrow();
        assertEquals("Text with ; and \"quotes\"", element.attributes().get("definition"));
    }

    @Test
    void rejectsMissingRequiredFieldsAndMalformedQuotes() throws Exception {
        InMemorySilaRepositoryStub repository = new InMemorySilaRepositoryStub();
        GlossaryImportService service = new GlossaryImportService(repository);
        String input = """
                ;No term;Demo
                NoDefinition;;Demo
                "Unclosed;Definition;Demo
                """;

        var result = service.importSemicolonSeparated(new StringReader(input));

        assertEquals(3, result.rejected());
        assertEquals(0, repository.findElements().size());
    }
}
