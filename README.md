# Colonists Workspace

This repository is organized as a local workspace containing multiple mod folders.

## Colonists implementation

- Active implementation folder: `Colonists/`
- Design spec: `Colonists/docs/colony-v1.md`

## Other local mod folders (ignored by this repo)

- `SimpleClaims-RR/`
- `hytale-template-plugin/`
- `DuelArena/`
- `fight-caves/` (standalone repo: https://github.com/Shieldudaram/fight-caves)
- `devkit/` (standalone repo: https://github.com/Shieldudaram/Devkit)
- `CustomTree/` (standalone repo: https://github.com/Shieldudaram/CustomTree)

## Build Tooling

Shared workspace scripts are intentionally not tracked in this repository.

For Colonists builds and validation, run commands from `Colonists/`:

- `./gradlew test`
- `./gradlew jar hytaleJar installToHytaleMods verifyInstalledModJar`
