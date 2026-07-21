package dev.lokspel.deathswap.game;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

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
        return PlayerUtil.getOnlinePlayers(players);
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

            for (Player player : PlayerUtil.getOnlinePlayers(players)) {
                PlayerUtil.showCountdownTitle(player, msg);
            }

            remainingCountdown--;
        }, 0L, 20L);
    }

    private void restoreGameMode(UUID uuid) {
        Player player = PlayerUtil.getOnlinePlayer(uuid);
        if (player == null) return;
        player.setGameMode(previousGameModes.getOrDefault(uuid, GameMode.SURVIVAL));
        previousGameModes.remove(uuid);
    }
}
