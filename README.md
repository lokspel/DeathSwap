# DeathSwap

Last-player-standing minigame. Join a lobby, teleport to a fresh world, swap positions with a random alive opponent every interval, and stay alive until only you remain.

## Features

- Auto-start lobby with configurable player minimums
- Timed position swaps with warning countdown
- Death tracking with configurable lives; last one standing wins
- Optional match time limit with winner-by-fewest-deaths (and tie messaging)
- Toggleable sidebar scoreboard and HUD countdowns
- Scoped match visibility (chat, death, advancement messages) across concurrent matches
- Optional tab-list hiding of in-match players (requires PacketEvents)
- PvP toggle, configurable sounds
- Fresh temporary world per match, deleted on game end
- Optional per-match nether and end dimensions (own portals per game)
- Configurable per-world random respawn radius around spawn

## Commands

| Command | Aliases | Description |
|---|---|---|
| `/deathswap join` | `/ds join` | Join the lobby |
| `/deathswap leave` | `/ds leave` | Leave the lobby or match |
| `/deathswap start` | `/ds start` | Force-start the game (`deathswap.start`, requires lobby to be set) |
| `/deathswap stop` | `/ds stop` | Stop the game (`deathswap.stop`) |
| `/deathswap setlobby` | `/ds setlobby` | Set the lobby location (`deathswap.setlobby`) |
| `/deathswap reload` | `/ds reload` | Reload config and messages (`deathswap.reload`) |

## Placeholders

Requires [PlaceholderAPI](https://placeholderapi.com). The expansion registers under both `deathswap` and `ds`, so a placeholder can be written as `%deathswap_<name>%` or `%ds_<name>%`.

| Placeholder | Description |
|---|---|
| `%deathswap_state%` | Player's game state: `none`, `lobby`, `match` or `spectator` |
| `%deathswap_deaths%` | Current death count in the match (`0` if not in a match) |
| `%deathswap_deaths_left%` | Deaths remaining before elimination |
| `%deathswap_max_deaths%` | `max-deaths` configured limit |
| `%deathswap_players_in_lobby%` | Players currently waiting in the lobby |
| `%deathswap_min_players%` | `min-players-to-start` required to start |
| `%deathswap_swap_interval%` | `swap-interval` configured seconds between swaps |
| `%deathswap_next_swap%` | Live countdown (seconds) until the next swap (`0` = swapping now; full interval outside a match) |

## Building

Requires Java 25 and Maven.

```sh
mvn clean package
```

The output jar is `target/DeathSwap-1.0.4.jar`.

## Requirements

- Spigot or Paper 1.21.11+
- Java 25
- Optional: [PacketEvents](https://github.com/retrooper/packetevents) for the `hide.match-players-in-tab` feature. When not installed, the plugin still works; only tab-list hiding is unavailable.

## Tab hiding (PacketEvents)

Scoped match visibility isolates players across concurrent matches: chat, death and advancement messages only reach players in the same match. When `hide.match-players-in-tab` is enabled (and PacketEvents is installed), in-match players are also removed from the tab list of players outside their match — a lobby player can't see match participants, and each match only shows its own members. The plugin reloads these settings on `/deathswap reload`, including for already-running matches.

> **Note:** Isolation of advancement messages requires Paper; on Spigot this feature is unavailable.
