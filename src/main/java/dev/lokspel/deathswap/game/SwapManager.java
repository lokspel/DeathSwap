package dev.lokspel.deathswap.game;

import dev.lokspel.deathswap.DeathSwap;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
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
        long delay = Math.max(1,
            (long) plugin.getConfigManager().swapInterval() - plugin.getConfigManager().countdownSeconds()
        ) * 20L;

        delayTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            delayTask = null;
            if (aliveSupplier.get().size() < 2) return;
            startCountdown(onSwapComplete, aliveSupplier);
        }, delay);
    }

    private void startCountdown(Runnable onSwapComplete, Supplier<Set<Player>> aliveSupplier) {
        var cfg = plugin.getConfigManager();
        countdownRemaining = cfg.countdownSeconds();

        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (countdownRemaining <= 0) {
                onSwapComplete.run();
                countdownTask.cancel();
                countdownTask = null;
                return;
            }

            var msg = cfg.getMessages().message("countdown", "seconds", String.valueOf(countdownRemaining));
            var title = Title.title(msg, Component.empty(),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO));
            var tickSound = sound(cfg.countdownTickSound());

            for (Player player : aliveSupplier.get()) {
                player.showTitle(title);
                player.playSound(tickSound);
            }

            countdownRemaining--;
        }, 0L, 20L);
    }

    public void executeSwap(Set<Player> alivePlayers) {
        if (alivePlayers.size() < 2) return;

        var cfg = plugin.getConfigManager();
        var playerList = new ArrayList<>(alivePlayers);
        var loc1 = playerList.get(0).getLocation();
        var loc2 = playerList.get(1).getLocation();
        var goSound = sound(cfg.countdownGoSound());
        var swapSound = sound(cfg.swapSound());

        for (Player player : playerList) {
            player.playSound(goSound);
        }

        playerList.get(0).teleport(loc2);
        playerList.get(1).teleport(loc1);

        for (Player player : playerList) {
            player.playSound(swapSound);
            player.sendMessage(cfg.getMessages().prefixed("swap-message"));
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

    private Sound sound(String key) {
        return Sound.sound(NamespacedKey.minecraft(key), Sound.Source.MASTER, 1.0f, 1.0f);
    }
}
