# Colonists Workspace

This repository tracks Colonists ownership and release flow.

## Colonists implementation

- Active implementation folder: `Colonists/`
- Design spec: `Colonists/docs/colony-v1.md`

## Repository Boundaries

- Tracked mod ownership in this repository is focused on Colonists.
- External sibling repos can exist in the same local workspace, but they are not tracked here.
- `fight-caves/` is maintained in its own repository: https://github.com/Shieldudaram/fight-caves
- `hytalecraft/` is maintained in its own repository: https://github.com/Shieldudaram/hytalecraft-RR

## Build Tooling

Shared workspace scripts are intentionally not tracked in this repository.

For Colonists builds and validation, run commands from `Colonists/`:

- `./gradlew test`
- `./gradlew jar hytaleJar installToHytaleMods verifyInstalledModJar`
