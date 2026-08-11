package dev.lokspel.deathswap.game;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.config.MainConfig;
import dev.lokspel.deathswap.config.MessagesConfig;
import dev.lokspel.deathswap.scoreboard.MatchScoreboard;
import dev.lokspel.deathswap.player.PlayerState;
import dev.lokspel.deathswap.player.PlayerStateManager;
import dev.lokspel.deathswap.util.PlayerUtil;
import dev.lokspel.deathswap.util.SoundUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MatchManager {

    private final DeathSwap plugin;
    private final MainConfig cfg;
    private final MessagesConfig messages;
    private final World gameWorld;
    private final Set<UUID> playerUuids;
    private final Set<UUID> spectators;
    private final PlayerStateManager states;
    private final DeathManager deaths;
    private final SwapManager swap;
    private final MatchScoreboard scoreboard;
    private final Runnable onEnd;
    private boolean cleanedUp;

    public MatchManager(DeathSwap plugin, List<Player> players, Runnable onEnd) {
        this.plugin = plugin;
        this.cfg = plugin.getMainConfig();
        this.messages = cfg.messages();
        this.onEnd = onEnd;
        this.playerUuids = new HashSet<>();
        this.spectators = new HashSet<>();
        this.states = new PlayerStateManager();
        this.deaths = new DeathManager();
        this.swap = new SwapManager(plugin);
        this.scoreboard = new MatchScoreboard(plugin);
        this.cleanedUp = false;

        for (Player player : players) {
            playerUuids.add(player.getUniqueId());
            player.sendMessage(messages.prefixed("world-preparing"));
        }

        gameWorld = plugin.getWorldPool().createGameWorld();

        for (Player player : players) {
            player.teleport(gameWorld.getSpawnLocation());
            states.save(player);
            PlayerState.resetForMatch(player);
        }

        deaths.init(playerUuids);
        scoreboard.init(messages.get("scoreboard-title"));
        refreshScoreboard();

        scheduleNextSwap();
        broadcast(messages.prefixed("game-started"));
    }

    /**
     * Handles a respawn of an in-match player. Returns the location to respawn
     * at (the game world spawn), or {@code null} if the player is not in the
     * match. The caller must apply it via {@code PlayerRespawnEvent#setRespawnLocation},
     * as a direct teleport is overridden by the event afterward.
     */
    public Location onPlayerRespawn(Player player) {
        if (!playerUuids.contains(player.getUniqueId())) return null;

        if (spectators.contains(player.getUniqueId())) {
            player.setGameMode(GameMode.SPECTATOR);
        }

        Location location = gameWorld.getSpawnLocation();
        refreshScoreboard();

        if (spectators.contains(player.getUniqueId())) {
            checkWinner();
        }
        return location;
    }

    public void onPlayerDeath(Player player) {
        if (!playerUuids.contains(player.getUniqueId())) return;

        int deathCount = deaths.add(player.getUniqueId());
        int max = cfg.game().maxDeaths();

        if (deathCount >= max) {
            spectators.add(player.getUniqueId());
            player.sendMessage(messages.prefixed("eliminated"));
            broadcast(messages.prefixed("player-eliminated", "player", player.getName()));
            checkWinner();
        } else {
            player.sendMessage(messages.prefixed("deaths-left", "deaths", String.valueOf(max - deathCount)));
        }

        refreshScoreboard();
    }

    public void leave(Player player, boolean teleport) {
        if (!playerUuids.contains(player.getUniqueId())) return;

        playerUuids.remove(player.getUniqueId());
        spectators.remove(player.getUniqueId());
        deaths.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

        if (teleport) {
            plugin.getWorldPool().teleportToLobby(player);
        }

        refreshScoreboard();
        checkWinner();
    }

    public boolean contains(UUID uuid) {
        return playerUuids.contains(uuid);
    }

    public Set<Player> getOnlinePlayers() {
        return PlayerUtil.getOnlinePlayers(playerUuids);
    }

    public void broadcast(Component message) {
        for (Player player : getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public void broadcastSound(String soundKey) {
        var sound = SoundUtil.minecraft(soundKey);
        for (Player player : getOnlinePlayers()) {
            player.playSound(sound);
        }
    }

    private void refreshScoreboard() {
        scoreboard.update(deaths.getAll());
        scoreboard.apply(deaths.getAll().keySet());
    }

    private void scheduleNextSwap() {
        if (deaths.size() < 2) return;
        swap.scheduleNext(this::onSwapComplete, deaths::getAlivePlayers);
    }

    private void onSwapComplete() {
        Set<Player> alive = deaths.getAlivePlayers();

        if (alive.size() < 2) {
            checkWinner();
            return;
        }

        swap.executeSwap(alive);
        scheduleNextSwap();
    }

    private void checkWinner() {
        Set<Player> alive = deaths.getAlivePlayers();
        if (alive.size() > 1) return;

        cancelTasks();

        if (alive.size() == 1) {
            Player winner = alive.iterator().next();
            broadcast(messages.prefixed("winner", "player", winner.getName()));
            broadcastSound(cfg.sounds().win());
            winner.setGameMode(GameMode.SURVIVAL);
        }

        cleanupNow();
    }

    public void stop() {
        cancelTasks();
        cleanupNow();
    }

    private void cleanupNow() {
        if (cleanedUp) return;
        cleanedUp = true;

        scoreboard.remove(playerUuids);
        for (Player player : getOnlinePlayers()) {
            states.restore(player);
            plugin.getWorldPool().teleportToLobby(player);
        }

        plugin.getWorldPool().deleteWorld(gameWorld);
        deaths.clear();
        states.clear();
        spectators.clear();
        playerUuids.clear();
        onEnd.run();
    }

    private void cancelTasks() {
        swap.cancel();
    }
}
