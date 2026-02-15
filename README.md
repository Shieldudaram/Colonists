# Colonists Workspace

This repository tracks Colonists ownership and release flow.

## Colonists implementation

- Active implementation folder: `Colonists/`
- Design spec: `Colonists/docs/colony-v1.md`

## Repository Boundaries

- Tracked mod ownership in this repository is focused on Colonists.
- External sibling repos can exist in the same local workspace, but they are not tracked here.
- Parent `.gitignore` uses a strict top-level allowlist model: root directories are ignored by default, then only repository-owned roots are unignored.
- `fight-caves/` is maintained in its own repository: https://github.com/Shieldudaram/fight-caves
- `hytalecraft/` is maintained in its own repository: https://github.com/Shieldudaram/hytalecraft-RR

### Boundary Maintenance Checks

From `/Users/chrisjennison/Desktop/Everything Hytale/Ruler Of The Realms/RealmRulerMods`:

- `git check-ignore -v scripts/`
- `printf 'new-test-mod/\n' | git check-ignore -v --stdin`
- `git ls-tree --name-only HEAD`
- `./scripts/verify-module-boundaries.sh`
- `./scripts/verify-workspace-script-boundaries.sh --workspace-root "/Users/chrisjennison/Desktop/Everything Hytale/Ruler Of The Realms/RealmRulerMods"`

### Global Git Excludes

Set machine-level OS/editor safety excludes:

- `git config --global core.excludesfile "/Users/chrisjennison/.gitignore_global"`
- `git config --global --get core.excludesfile`

## Build Tooling

Shared workspace scripts are intentionally not tracked in this repository.

For Colonists builds and validation, run commands from `Colonists/`:

- `./gradlew test`
- `./gradlew jar hytaleJar installToHytaleMods verifyInstalledModJar`
