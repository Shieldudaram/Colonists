# Colonists v1 Design

## Core decisions

- Simulation tick: `5 Hz` default, runtime toggles enabled.
- Colony model: one persistent colony, no full district system in v1.
- Limits at launch:
  - `max_citizens=5`
  - `max_hotspots_per_family=1`
  - `max_active_raid_enemies=1`
- Needs: `food`, `rest`, `safety` with speed-only penalties.
- Tasking: fully autonomous with global priority sliders; mid-route preemption allowed.
- Raids: mixed scheduled and threat-triggered cadence; normal raiders target Town Core first.
- Combat rule: no friendly fire.
- Insurance: guaranteed immediate replacement at Town Core with copied skills.
- Processing: globally shared stations.
- Pause mode: supported for queue-and-plan play.

## Hotspot model

- Hotspots are placed at Tier 1 and can be upgraded to Tier 3.
- First successful harvest starts a reset timer.
- Harvesting depletes capacity and degrades yield.
- At timer expiry, the hotspot returns to full capacity and full yield.
- Families in scope: `wood`, `stone`, `ore`, `fiber`, `food`, `hide`, `herbs`, `crystal`.

### Upgrade defaults

- `T1 -> T2`: `30 wood`, `20 stone`, `10 fiber`
- `T2 -> T3`: `50 wood`, `35 stone`, `20 ore`, `10 crystal`
- Upgrade application is instant once paid.

### Reset defaults

- `T1`: `10m`
- `T2`: `8m`
- `T3`: `6m`

## Milestones

1. Core sim loop, pause planner, task broker, save/backups.
2. Economy keys (tier/quality), stockpile logic, hotspot lifecycle.
3. Housing cap, needs model, construction pipeline, farming.
4. Raid director, guard/tower/trap systems, repair loops.
5. Insurance claims, commander policies, replayable crises.
6. JSON content packs, script callbacks, chat telemetry commands.

## Content architecture

- Data-first definitions for:
  - hotspots
  - recipes
  - events
  - raid factions
  - commander policies
- Script callback hooks exposed for mod extension points.
