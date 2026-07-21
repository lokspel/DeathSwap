package dev.lokspel.deathswap.game;

import dev.lokspel.deathswap.DeathSwap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LobbyManager {

    private final DeathSwap plugin;
    private final Set<UUID> players = new HashSet<>();
    private final Map<UUID, GameMode> previousGameModes = new HashMap<>();
    private BukkitTask startTask;
    private int remainingCountdown;

    public LobbyManager(DeathSwap plugin) {
        this.plugin = plugin;
    }

    public void join(Player player) {
        if (!players.add(player.getUniqueId())) return;

        previousGameModes.put(player.getUniqueId(), player.getGameMode());
        player.setGameMode(GameMode.SURVIVAL);
        player.sendMessage(plugin.getConfigManager().getMessages().prefixed("joined"));

        if (startTask != null && players.size() >= plugin.getConfigManager().minPlayersFastStart()) {
            int target = plugin.getConfigManager().fastStartDelay();
            if (remainingCountdown > target) {
                remainingCountdown = target;
            }
        }
    }

    public void leave(UUID uuid) {
        if (!players.remove(uuid)) return;
        restoreGameMode(uuid);
        cancelTask();
    }

    public boolean contains(UUID uuid) {
        return players.contains(uuid);
    }

    public Set<Player> getOnlinePlayers() {
        Set<Player> online = new HashSet<>();
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                online.add(player);
            }
        }
        return online;
    }

    public int size() {
        return players.size();
    }

    public void clear() {
        cancelTask();
        players.forEach(this::restoreGameMode);
        previousGameModes.clear();
        players.clear();
    }

    public void cancelTask() {
        if (startTask == null) return;
        startTask.cancel();
        startTask = null;
    }

    public void tryAutoStart(Runnable onStart) {
        var cfg = plugin.getConfigManager();
        if (startTask != null || players.size() < cfg.minPlayersToStart()) return;

        remainingCountdown = cfg.startDelay();
        if (players.size() >= cfg.minPlayersFastStart()) {
            remainingCountdown = Math.min(remainingCountdown, cfg.fastStartDelay());
        }

        startTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (remainingCountdown <= 0) {
                cancelTask();
                onStart.run();
                return;
            }

            if (players.size() < cfg.minPlayersToStart()) {
                cancelTask();
                return;
            }

            var msg = cfg.getMessages().message("starting", "seconds", String.valueOf(remainingCountdown - 1));
            var title = Title.title(msg, Component.empty(),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO));

            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    player.showTitle(title);
                }
            }

            remainingCountdown--;
        }, 0L, 20L);
    }

    private void restoreGameMode(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) return;
        player.setGameMode(previousGameModes.getOrDefault(uuid, GameMode.SURVIVAL));
        previousGameModes.remove(uuid);
    }
}
