package io.github.nesmachnydn.sila.service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

final class StableIds {
    private StableIds() {
    }

    static String fromExternalKey(String externalKey) {
        return UUID.nameUUIDFromBytes(externalKey.getBytes(StandardCharsets.UTF_8)).toString();
    }
}
