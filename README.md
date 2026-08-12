# DeathSwap

Last-player-standing minigame for Paper. Join a lobby, teleport to a fresh world, swap positions with a random alive opponent every interval, and stay alive until only you remain.

## Features

- Auto-start lobby with configurable player minimums
- Timed position swaps with warning countdown
- Death tracking with configurable lives; last one standing wins
- Toggleable sidebar scoreboard and HUD countdowns
- Scoped match chat, PvP toggle, configurable sounds
- Fresh temporary world per match, deleted on game end

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
game:
  min-players-to-start: 2
  start-delay: 120
  min-players-fast-start: 4
  fast-start-delay: 20
  swap-interval: 300
  countdown-seconds: 5
  max-deaths: 5
  pvp-enabled: false

  # Display Options
  scoreboard-enabled: true   # Show the in-match sidebar scoreboard
  actionbar-enabled: true    # Show HUD countdowns (lobby start + swap warning)

sounds:
  countdown-tick: entity.note.pling
  countdown-go: entity.experience_orb.pickup
  swap: entity.enderman.teleport
  win: ui.toast.challenge_complete
```

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

The output jar is `target/DeathSwap-1.0.0.jar`.

## Requirements

- Paper 1.21.11+ (api-version 26.2)
- Java 25
