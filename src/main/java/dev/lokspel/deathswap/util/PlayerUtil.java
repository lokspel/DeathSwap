package dev.lokspel.deathswap.util;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

public final class PlayerUtil {

    private PlayerUtil() {}

    public static Set<Player> getOnlinePlayers(Set<UUID> uuids) {
        var online = new java.util.HashSet<Player>();
        for (UUID uuid : uuids) {
            Player player = getOnlinePlayer(uuid);
            if (player != null) online.add(player);
        }
        return online;
    }

    public static Player getOnlinePlayer(UUID uuid) {
        Player player = org.bukkit.Bukkit.getPlayer(uuid);
        return (player != null && player.isOnline()) ? player : null;
    }

    public static void resetToSurvival(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(5.0f);
        player.setFireTicks(0);
        player.setRemainingAir(player.getMaximumAir());
    }

    public static void showCountdownTitle(Player player, Component message) {
        player.showTitle(Title.title(message, Component.empty(),
            Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)));
    }
}
