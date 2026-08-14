package io.github.nesmachnydn.sila.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal single-line parser for semicolon-separated demo data.
 * Supports quoted fields and escaped double quotes; multiline fields are intentionally unsupported.
 */
final class SemicolonSeparatedValues {
    private SemicolonSeparatedValues() {
    }

    static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (ch == ';' && !quoted) {
                fields.add(current.toString().strip());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        if (quoted) {
            throw new IllegalArgumentException("Unclosed quoted field");
        }

        fields.add(current.toString().strip());
        return List.copyOf(fields);
    }
}
