package dev.lokspel.deathswap.util;

import net.kyori.adventure.sound.Sound;
import org.bukkit.NamespacedKey;

public final class SoundUtil {

    private SoundUtil() {}

    public static Sound minecraft(String key) {
        return Sound.sound(NamespacedKey.minecraft(key), Sound.Source.MASTER, 1.0f, 1.0f);
    }
}
