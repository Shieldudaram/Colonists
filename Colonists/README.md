# Colonists Mod

This folder contains the implementation of the Colonists colony-simulation mod.

## Structure

- `src/main/java` - runtime, systems, command router, save/content loaders
- `src/main/resources/content/colonists` - data-driven packs (`config`, `hotspots`, `recipes`, `events`, `raid_factions`, `policies`)
- `src/test/java` - core behavior tests
- `docs/colony-v1.md` - v1 gameplay/system design spec

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

## Validation

Run tests from this folder:

`./gradlew test`
