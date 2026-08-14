package io.github.nesmachnydn.sila.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoApplicationTest {

    @Test
    void runsWithoutNetworkOrExternalConfiguration() throws Exception {
        String output = DemoApplication.runDemo();

        assertTrue(output.contains("Glossary import: created=4"));
        assertTrue(output.contains("Architecture Repository Demo"));
        assertTrue(output.contains("Customer Portal"));
        assertTrue(output.contains("GLOSSARY_TERM"));
    }
}
