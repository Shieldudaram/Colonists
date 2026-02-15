package com.shieldudaram.colonists.save;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.shieldudaram.colonists.model.ColonyState;
import com.shieldudaram.colonists.sim.ColonistsConstants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class ColonySaveService {
    private final ObjectMapper mapper;
    private final ColonySaveMapper mapperService;

    public ColonySaveService() {
        this.mapper = JsonMapper.builder()
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .build();
        this.mapperService = new ColonySaveMapper();
    }

    public void save(ColonyState state, Path saveDir) {
        try {
            Files.createDirectories(saveDir);
            Path active = saveDir.resolve("active-save.json");
            rotateBackups(saveDir, active);
            ColonySaveV1 save = mapperService.toSave(state);
            mapper.writeValue(active.toFile(), save);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to save colony", exception);
        }
    }

    public void load(ColonyState state, Path saveDir) {
        Path active = saveDir.resolve("active-save.json");
        if (!Files.exists(active)) {
            throw new IllegalStateException("No save file found at " + active);
        }

        try {
            ColonySaveV1 save = mapper.readValue(active.toFile(), ColonySaveV1.class);
            if (save.schemaVersion != ColonistsConstants.SCHEMA_VERSION) {
                throw new IllegalStateException(
                        "Save schema mismatch. Expected " + ColonistsConstants.SCHEMA_VERSION
                                + ", found " + save.schemaVersion
                                + ". Restore from backup in " + saveDir
                );
            }
            mapperService.applySave(state, save);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load colony save", exception);
        }
    }

    private void rotateBackups(Path saveDir, Path active) throws IOException {
        for (int index = ColonistsConstants.AUTOSAVE_ROTATIONS; index >= 1; index--) {
            Path source = index == 1 ? active : saveDir.resolve("backup-" + (index - 1) + ".json");
            Path target = saveDir.resolve("backup-" + index + ".json");
            if (Files.exists(source)) {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
