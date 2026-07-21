package dev.lokspel.deathswap.game;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.config.ConfigManager;
import dev.lokspel.deathswap.config.section.MessagesSection;
import dev.lokspel.deathswap.scoreboard.MatchScoreboard;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MatchManager {

    private final DeathSwap plugin;
    private final ConfigManager cfg;
    private final MessagesSection messages;
    private final World gameWorld;
    private final Set<UUID> playerUuids;
    private final DeathManager deaths;
    private final SwapManager swap;
    private final MatchScoreboard scoreboard;
    private final Runnable onEnd;
    private boolean cleanedUp;

    public MatchManager(DeathSwap plugin, List<Player> players, Runnable onEnd) {
        this.plugin = plugin;
        this.cfg = plugin.getConfigManager();
        this.messages = cfg.getMessages();
        this.onEnd = onEnd;
        this.playerUuids = new HashSet<>();
        this.deaths = new DeathManager();
        this.swap = new SwapManager(plugin);
        this.scoreboard = new MatchScoreboard(plugin);
        this.cleanedUp = false;

        for (Player player : players) {
            playerUuids.add(player.getUniqueId());
            player.sendMessage(messages.prefixed("world-preparing"));
        }

        gameWorld = plugin.getWorldManager().createGameWorld();

        for (Player player : players) {
            player.teleport(gameWorld.getSpawnLocation());
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setSaturation(5.0f);
            player.setFireTicks(0);
            player.setRemainingAir(player.getMaximumAir());
        }

        deaths.init(playerUuids);

        scoreboard.init(messages.message("scoreboard-title"));
        scoreboard.update(deaths.getAll());
        scoreboard.apply(deaths.getAll().keySet());

        scheduleNextSwap();
        broadcast(messages.prefixed("game-started"));
    }

    public void onPlayerRespawn(Player player) {
        if (!deaths.contains(player.getUniqueId())) return;
        player.teleport(gameWorld.getSpawnLocation());
    }

    public void onPlayerDeath(Player player) {
        if (!deaths.contains(player.getUniqueId())) return;
        if (player.getGameMode() == GameMode.SPECTATOR) return;

        int deathCount = deaths.add(player.getUniqueId());
        int max = cfg.maxDeaths();

        if (deathCount >= max) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(messages.prefixed("eliminated"));
            broadcast(messages.prefixed("player-eliminated", "player", player.getName()));
            checkWinner();
        } else {
            player.sendMessage(messages.prefixed("deaths-left", "deaths", String.valueOf(max - deathCount)));
        }

        scoreboard.update(deaths.getAll());
        scoreboard.apply(deaths.getAll().keySet());
    }

    public void leave(Player player, boolean teleport) {
        if (!deaths.contains(player.getUniqueId())) return;

        playerUuids.remove(player.getUniqueId());
        deaths.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

        if (teleport) {
            plugin.getWorldManager().teleportToLobby(player);
        }

        scoreboard.update(deaths.getAll());
        scoreboard.apply(deaths.getAll().keySet());
        checkWinner();
    }

    public boolean contains(UUID uuid) {
        return playerUuids.contains(uuid);
    }

    public Set<Player> getOnlinePlayers() {
        Set<Player> online = new HashSet<>();

        for (UUID uuid : playerUuids) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                online.add(player);
            }
        }

        return online;
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

            Sound winSound = Sound.sound(
                NamespacedKey.minecraft(cfg.winSound()),
                Sound.Source.MASTER, 1.0f, 1.0f);
            for (Player p : getOnlinePlayers()) {
                p.playSound(winSound);
            }

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
            player.setGameMode(GameMode.SURVIVAL);
            plugin.getWorldManager().teleportToLobby(player);
        }

        plugin.getWorldManager().deleteWorld(gameWorld);
        deaths.clear();
        playerUuids.clear();

        if (onEnd != null) onEnd.run();
    }

    public void broadcast(net.kyori.adventure.text.Component message) {
        for (Player player : getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    private void cancelTasks() {
        swap.cancel();
    }
}
