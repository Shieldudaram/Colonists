package com.shieldudaram.colonists.save;

import com.shieldudaram.colonists.model.ColonyState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ColonySaveServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void schemaMismatchFailsLoad() throws IOException {
        Path saveDir = tempDir.resolve("saves");
        Files.createDirectories(saveDir);

        String mismatch = "{\"schemaVersion\":999,\"worldTimeSec\":0,\"colony\":{},\"citizens\":[],\"hotspots\":[],\"tasks\":[],\"raid\":{},\"insurance\":{}}";
        Files.writeString(saveDir.resolve("active-save.json"), mismatch);

        ColonySaveService service = new ColonySaveService();
        assertThrows(IllegalStateException.class, () -> service.load(new ColonyState(), saveDir));
    }
}
