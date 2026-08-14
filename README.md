# DeathSwap

Last-player-standing minigame for Paper. Join a lobby, teleport to a fresh world, swap positions with a random alive opponent every interval, and stay alive until only you remain.

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
| `/deathswap start` | `/ds start` | Force-start the game (`deathswap.start`) |
| `/deathswap stop` | `/ds stop` | Stop the game (`deathswap.stop`) |
| `/deathswap setlobby` | `/ds setlobby` | Set the lobby location (`deathswap.setlobby`) |
| `/deathswap reload` | `/ds reload` | Reload config and messages (`deathswap.reload`) |

## Configuration

### `config.yml`

```yaml
# DeathSwap Configuration

game:
  # Start Countdown — configures the pre-game lobby timer
  min-players-to-start: 2     # Players needed for countdown to begin
  start-delay: 120            # Countdown in seconds (2 min = 120)
  min-players-fast-start: 4   # At 4+ players timer shrinks
  fast-start-delay: 20        # Shortened countdown in seconds

  # In-Game Rules
  swap-interval: 300          # Seconds between swaps
  countdown-seconds: 5        # Warning countdown before each swap
  max-deaths: 5               # Deaths before elimination
  max-match-time: 0           # Max match length in minutes, 0 = unlimited
  pvp-enabled: false          # Allow players to damage each other

display:
  scoreboard: true           # Show the in-match sidebar scoreboard
  actionbar: true            # Show HUD countdowns (lobby start + swap warning)

hide:
  isolate-chat: true          # Keep chat between players in the same match
  isolate-deaths: true        # Keep death messages between players in the same match
  isolate-achievements: true  # Keep advancement messages between players in the same match
  match-players-in-tab: false # Hide players in a match from the tab list of players outside that match (requires PacketEvents)

sounds:
  countdown-tick: entity.note.pling
  countdown-go: entity.experience_orb.pickup
  swap: entity.enderman.teleport
  win: ui.toast.challenge_complete

worlds:
  count: 5                # Number of persistent reusable worlds (each = one concurrent match)
  pre-generate-radius: 7  # Pre-generate spawn chunks around the spawn, 0 = off
  spawn-radius: 100       # Per-world random respawn radius around spawn, 0 = exact spawn
  border: 3000             # World border size in blocks (centered on spawn), 0 = disabled
  name-prefix: ds  # Prefix for generated world names (e.g. "ds_0")
  generate-dimensions: true   # Give every game world its own nether and end (false = share server defaults)
```

> When `max-match-time` is reached, the alive player with the fewest deaths wins; if tied, the `time-up` message lists the players.

### `messages.yml`

All messages in MiniMessage. Insert the prefix anywhere with `%prefix%`; placeholders like `<seconds>`, `<player>`, `<deaths>`, `<players>`, `<required>` are replaced at runtime.

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

The output jar is `target/DeathSwap-1.0.1.jar`.

## Requirements

- Paper 1.21.11+ (api-version 26.2)
- Java 25
- Optional: [PacketEvents](https://github.com/retrooper/packetevents) for the `hide.match-players-in-tab` feature. When not installed, the plugin still works; only tab-list hiding is unavailable.

## Tab hiding (PacketEvents)

Scoped match visibility isolates players across concurrent matches: chat, death and advancement messages only reach players in the same match. When `hide.match-players-in-tab` is enabled (and PacketEvents is installed), in-match players are also removed from the tab list of players outside their match — a lobby player can't see match participants, and each match only shows its own members. The plugin reloads these settings on `/deathswap reload`, including for already-running matches.
