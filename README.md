# Colonists Workspace

This repository is organized as a local workspace containing multiple mod folders.

## Colonists implementation

- Active implementation folder: `Colonists/`
- Design spec: `Colonists/docs/colony-v1.md`

## Other local mod folders (ignored by this repo)

- `SimpleClaims-RR/`
- `hytale-template-plugin/`
- `DuelArena/`
- `devkit/` (standalone repo: https://github.com/Shieldudaram/Devkit)

## Build All Mods

Use one command to build all Gradle-based mods in this workspace:

- `./scripts/build-all-mods.sh`

Common usage:

- Build only selected modules:
  - `./scripts/build-all-mods.sh --module DuelArena --module hytalecraft`
- Skip a module:
  - `./scripts/build-all-mods.sh --skip SimpleClaims-RR`
- Override Hytale install path:
  - `./scripts/build-all-mods.sh --hytale-home "$HOME/Library/Application Support/Hytale"`
- CI-style (do not require local Hytale install):
  - `./scripts/build-all-mods.sh --require-hytale false`
- Preview actions without running:
  - `./scripts/build-all-mods.sh --dry-run`
