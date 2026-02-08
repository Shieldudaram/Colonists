package com.shieldudaram.colonists.telemetry;

import com.shieldudaram.colonists.model.ColonyState;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class TelemetryService {
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);

    private TelemetryMode mode = TelemetryMode.BRIEF;
    private final Path logDir;

    public TelemetryService(Path logDir) {
        this.logDir = logDir;
    }

    public TelemetryMode mode() {
        return mode;
    }

    public void setMode(TelemetryMode mode) {
        this.mode = mode;
    }

    public String status(ColonyState state, TelemetryMode requestedMode) {
        TelemetryMode effective = requestedMode == null ? mode : requestedMode;
        if (effective == TelemetryMode.OFF) {
            return "Telemetry is off.";
        }
        if (effective == TelemetryMode.BRIEF) {
            return String.format(
                    Locale.ROOT,
                    "status t=%ds pop=%d/%d hotspots=%d tasks=%d raidEnemies=%d policy=%s",
                    state.worldTimeSec(),
                    state.populationCurrent(),
                    state.populationCap(),
                    state.hotspots().size(),
                    state.tasks().size(),
                    state.raidState().activeEnemies(),
                    state.activePolicy().name()
            );
        }

        return "status\n"
                + " timeSec=" + state.worldTimeSec() + "\n"
                + " population=" + state.populationCurrent() + "/" + state.populationCap() + "\n"
                + " policy=" + state.activePolicy().name() + "\n"
                + " hotspots=" + state.hotspots().size() + "\n"
                + " tasks=" + state.tasks().size() + "\n"
                + " raid.activeEnemies=" + state.raidState().activeEnemies() + "\n"
                + " raid.nextRaidAtSec=" + state.raidState().nextRaidAtSec() + "\n"
                + " stock=[wood=" + state.stockWood()
                + ", stone=" + state.stockStone()
                + ", fiber=" + state.stockFiber()
                + ", food=" + state.stockFood()
                + "]";
    }

    public void log(String line) {
        if (mode == TelemetryMode.OFF) {
            return;
        }
        try {
            Files.createDirectories(logDir);
            String dayFile = "colonists-" + DAY_FORMAT.format(Instant.now()) + ".log";
            Path logFile = logDir.resolve(dayFile);
            String timestamped = Instant.now() + " " + line + System.lineSeparator();
            Files.writeString(
                    logFile,
                    timestamped,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            rotate();
        } catch (IOException ignored) {
        }
    }

    private void rotate() throws IOException {
        List<Path> files = Files.list(logDir)
                .filter(path -> path.getFileName().toString().startsWith("colonists-") && path.getFileName().toString().endsWith(".log"))
                .sorted(Comparator.comparing(Path::toString).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        int keep = 7;
        for (int index = keep; index < files.size(); index++) {
            Files.deleteIfExists(files.get(index));
        }
    }
}
