# Colonists Mod

This repository contains the Colonists colony-simulation mod and its release flow.

## Structure

- `src/main/java` - core runtime, systems, command router, save/content loaders
- `src/hytale/java` - Hytale plugin adapter (`ColonistsPlugin`, command bridge, tick bridge)
- `src/main/resources/manifest.json` - plugin manifest consumed by Hytale loader
- `src/main/resources/content/colonists` - bundled data-driven packs (`config`, `hotspots`, `recipes`, `events`, `raid_factions`, `policies`)
- `src/test/java` - core behavior tests
- `docs/colony-v1.md` - v1 gameplay/system design spec

## Repository Boundaries

- Tracked mod ownership in this repository is focused on Colonists.
- External sibling repos can exist in the same local workspace, but they are not tracked here.
- `fight-caves/` is maintained in its own repository: https://github.com/Shieldudaram/fight-caves
- `hytalecraft/` is maintained in its own repository: https://github.com/Shieldudaram/hytalecraft-RR

## Build and Install

Run from the repository root:

- `./gradlew test`
- `./gradlew jar hytaleJar installToHytaleMods verifyInstalledModJar`

Artifact outputs:

- `build/libs/colonists-<version>-core.jar` (core-only, not for Mods folder)
- `build/libs/colonists-<version>-hytale.jar` (plugin runtime jar for Mods folder)

The install task copies only the `-hytale.jar` artifact to your local Hytale mods folder and removes legacy non-hytale jars for this module.

## Plugin Metadata

- Manifest file: `src/main/resources/manifest.json`
- Plugin entry point: `com.shieldudaram.colonists.plugin.ColonistsPlugin`

## Runtime Data Paths

Colonists writes runtime files under the plugin data directory provided by Hytale:

- `config/colonists-config.json`
- `content/colonists/*.json` (seeded from bundled resources on first run)
- `logs/colonists/colonists-YYYY-MM-DD.log`
- `saves/colonists/active-save.json` and `backup-<n>.json`

## Commands

- `/colony status [--brief|--full]`
- `/colony tasks [--brief|--full]`
- `/colony hotspots [--brief|--full]`
- `/colony raid [--brief|--full]`
- `/colony priority set <build|farm|gather|haul|defend|repair> <0.50-2.00>`
- `/colony policy set <Fortify|HarvestRush|Recovery>`
- `/colony pause`
- `/colony resume`
- `/colony save`
- `/colony build place <TownCore|House|Stockpile|Watchtower|TrapPost|FarmShed|Workshop|Infirmary> <x> <z> <rotation>`
- `/colony hotspot place <family> <x> <z>`
- `/colony hotspot upgrade <hotspotId>`
- `/colony zone mark1 <x> <z>`
- `/colony zone mark2 <x> <z>`
- `/colony zone create <Home|Farm|Defense|Hotspot|Storage>`
- `/colony zone clear <zoneId>`
- `/colony crisis start bandit_assault`
- `/colony telemetry <brief|full|off>`

## Runtime Verification

- `jar tf "$HOME/Library/Application Support/Hytale/UserData/Mods/colonists-<version>-hytale.jar" | rg 'ColonistsPlugin.class|manifest.json'`
- `rg -n 'Colonists|Failed to load plugin|ClassNotFoundException' "$HOME/Library/Application Support/Hytale/UserData/Saves/TestWorld/logs"/*.log | tail -n 40`

Shared workspace scripts are intentionally not tracked in this repository.

## Quick Smoke Test

- Start the server with Colonists enabled in world mod selection.
- Run `/colony status`, `/colony pause`, `/colony resume`, `/colony telemetry full`, `/colony save`.
- Run `/colony zone mark1 0 0`, `/colony zone mark2 10 10`, `/colony zone create Home`.
- Run `/colony hotspot place wood 2 2` and `/colony hotspot upgrade <hotspotId>`.
- Confirm there are no manifest/plugin load errors in client/server logs.
