# DeathSwap

A Paper plugin that runs a last-player-standing minigame. Players join a lobby, get teleported to a fresh world, and periodically swap positions with a random alive opponent. Reach the death limit and you're out — last one standing wins.

## Features

- **Auto-start lobby** — Countdown begins when enough players join (2 default). At 4+ players the timer shortens automatically.
- **Timed position swaps** — Every configurable interval (default 180s), two random alive players swap locations after a 5-second warning countdown.
- **Death tracking** — Each player has a configurable number of lives (default 5). Reach zero and you become a spectator.
- **Scoreboard** — Live sidebar showing each player's death count, sorted by most deaths.
- **Scoped chat** — Players in a match only see messages from other match participants; non-match players can't see match chat.
- **PvP toggle** — Enable or disable player-vs-player damage via config.
- **Configurable sounds** — Countdown ticks, swap effects, and win fanfare are all configurable.
- **Per-message MiniMessage formatting** — All user-facing messages use MiniMessage and are fully customizable in `messages.yml`.
- **Temporary worlds** — Each match creates a fresh world that is automatically deleted when the game ends.

## Commands

| Command | Aliases | Permission | Description |
|---|---|---|---|
| `/deathswap join` | `/ds join` | — | Join the lobby |
| `/deathswap leave` | `/ds leave` | — | Leave the lobby or current match |
| `/deathswap start` | `/ds start` | `deathswap.start` | Force-start the game |
| `/deathswap stop` | `/ds stop` | `deathswap.stop` | Stop the current game |
| `/deathswap setlobby` | `/ds setlobby` | `deathswap.setlobby` | Set the lobby teleport location |
| `/deathswap reload` | `/ds reload` | `deathswap.reload` | Reload config and messages |

## Permissions

| Node | Default | Description |
|---|---|---|
| `deathswap.start` | `op` | Allows force-starting the game |
| `deathswap.stop` | `op` | Allows stopping the game |
| `deathswap.setlobby` | `op` | Allows setting the lobby location |
| `deathswap.reload` | `op` | Allows reloading the config |

## Configuration

### `config.yml`

```yaml
game:
  min-players-to-start: 2
  start-delay: 120
  min-players-fast-start: 4
  fast-start-delay: 20
  swap-interval: 180
  countdown-seconds: 5
  max-deaths: 5
  pvp-enabled: false

sounds:
  countdown-tick: entity.note.pling
  countdown-go: entity.experience_orb.pickup
  swap: entity.enderman.teleport
  win: ui.toast.challenge_complete
```

### `messages.yml`

All messages use [MiniMessage format](https://docs.advntr.dev/minimessage/). Placeholder tags like `<seconds>`, `<player>`, and `<deaths>` are replaced at runtime.

## Building

Requires Java 25 and Maven.

```sh
mvn clean package
```

The output jar is `target/DeathSwap-1.0.0.jar`.

## Requirements

- Paper 1.21.11+ (api-version 26.2)
- Java 25
