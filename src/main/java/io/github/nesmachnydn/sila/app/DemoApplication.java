package io.github.nesmachnydn.sila.app;

import io.github.nesmachnydn.sila.domain.ImportResult;
import io.github.nesmachnydn.sila.service.ArchitectureReportService;
import io.github.nesmachnydn.sila.service.GlossaryImportService;
import io.github.nesmachnydn.sila.stub.DemoRepositoryFixtures;
import io.github.nesmachnydn.sila.stub.InMemorySilaRepositoryStub;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Runs the entire portfolio demo without any external infrastructure.
 */
public final class DemoApplication {
    private DemoApplication() {
    }

    public static void main(String[] args) throws Exception {
        System.out.print(runDemo());
    }

    public static String runDemo() throws IOException {
        InMemorySilaRepositoryStub repository = DemoRepositoryFixtures.populatedRepository();
        GlossaryImportService glossaryImport = new GlossaryImportService(repository);

        ImportResult result;
        try (Reader glossary = openDemoGlossary()) {
            result = glossaryImport.importSemicolonSeparated(glossary);
        }

        ArchitectureReportService reportService = new ArchitectureReportService(repository);
        String report = reportService.generateMarkdown("Architecture Repository Demo");

        return "Glossary import: created=%d, updated=%d, unchanged=%d, rejected=%d%n%n%s"
                .formatted(result.created(), result.updated(), result.unchanged(), result.rejected(), report);
    }

    private static Reader openDemoGlossary() {
        var stream = DemoApplication.class.getResourceAsStream("/demo/glossary.csv");
        if (stream == null) {
            throw new IllegalStateException("Missing demo glossary resource");
        }
        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }
}
