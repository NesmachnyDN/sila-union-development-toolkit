package io.github.nesmachnydn.sila.service;

import io.github.nesmachnydn.sila.stub.DemoRepositoryFixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchitectureReportServiceTest {

    @Test
    void generatesDeterministicMarkdownFromSyntheticRepository() {
        var repository = DemoRepositoryFixtures.populatedRepository();
        ArchitectureReportService service = new ArchitectureReportService(repository);

        String report = service.generateMarkdown("Synthetic Architecture");

        assertTrue(report.startsWith("# Synthetic Architecture"));
        assertTrue(report.contains("| APPLICATION_COMPONENT | 3 |"));
        assertTrue(report.contains("Customer Portal"));
        assertTrue(report.contains("Document Service"));
        assertTrue(report.contains("authenticates-via"));
        assertTrue(report.contains("No external system is contacted"));
    }

    @Test
    void escapesMarkdownTableDelimiters() {
        var repository = DemoRepositoryFixtures.populatedRepository();
        ArchitectureReportService service = new ArchitectureReportService(repository);

        String report = service.generateMarkdown("Architecture | Demo");

        assertTrue(report.contains("# Architecture \\| Demo"));
    }
}
