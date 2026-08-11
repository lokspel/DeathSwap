package dev.lokspel.deathswap.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 * Creates or loads a game world by name. Must be called on the main thread:
 * {@code Bukkit.createWorld} requires it, but it is cheap because spawn-chunk
 * generation is disabled for our worlds.
 */
final class WorldLoader {

    World load(String name) {
        return Bukkit.createWorld(new WorldCreator(name));
    }
}
