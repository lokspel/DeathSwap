package dev.lokspel.deathswap.game;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.util.PlayerUtil;
import dev.lokspel.deathswap.util.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Supplier;

public class SwapManager {

    private final DeathSwap plugin;
    private BukkitTask delayTask;
    private BukkitTask countdownTask;
    private int countdownRemaining;

    public SwapManager(DeathSwap plugin) {
        this.plugin = plugin;
    }

    public void scheduleNext(Runnable onSwapComplete, Supplier<Set<Player>> aliveSupplier) {
        var cfg = plugin.getMainConfig();
        long delay = Math.max(1, (long) cfg.game().swapInterval() - cfg.game().countdownSeconds()) * 20L;

        delayTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            delayTask = null;
            if (aliveSupplier.get().size() < 2) return;
            startCountdown(onSwapComplete, aliveSupplier);
        }, delay);
    }

    private void startCountdown(Runnable onSwapComplete, Supplier<Set<Player>> aliveSupplier) {
        var cfg = plugin.getMainConfig();
        countdownRemaining = cfg.game().countdownSeconds();
        var messages = cfg.messages();
        var tickSound = SoundUtil.minecraft(cfg.sounds().countdownTick());
        boolean showHud = cfg.game().actionbarEnabled();

        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (countdownRemaining <= 0) {
                onSwapComplete.run();
                countdownTask.cancel();
                countdownTask = null;
                return;
            }

            var msg = messages.get("countdown", "seconds", String.valueOf(countdownRemaining));
            var alive = aliveSupplier.get();

            for (Player player : alive) {
                if (showHud) {
                    PlayerUtil.showCountdownTitle(player, msg);
                }
                player.playSound(tickSound);
            }

            countdownRemaining--;
        }, 0L, 20L);
    }

    public void executeSwap(Set<Player> alivePlayers) {
        if (alivePlayers.size() < 2) return;

        var cfg = plugin.getMainConfig();
        var messages = cfg.messages();
        var goSound = SoundUtil.minecraft(cfg.sounds().countdownGo());
        var swapSound = SoundUtil.minecraft(cfg.sounds().swap());

        var playerList = new ArrayList<>(alivePlayers);
        var loc1 = playerList.get(0).getLocation();
        var loc2 = playerList.get(1).getLocation();

        for (Player player : playerList) {
            player.playSound(goSound);
        }

        playerList.get(0).teleport(loc2);
        playerList.get(1).teleport(loc1);

        for (Player player : playerList) {
            player.playSound(swapSound);
            player.sendMessage(messages.prefixed("swap-message"));
        }
    }

    public void cancel() {
        if (delayTask != null) {
            delayTask.cancel();
            delayTask = null;
        }
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
    }
}
