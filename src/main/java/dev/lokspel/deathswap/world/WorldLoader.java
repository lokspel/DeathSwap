package dev.lokspel.deathswap.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 * Creates or loads a game world by name. Must be called on the main thread:
 * {@code Bukkit.createWorld} requires it. Modern Paper no longer pre-generates
 * a large spawn area synchronously, so world creation is cheap; the spawn
 * region is pre-generated asynchronously afterwards.
 */
final class WorldLoader {

    World load(String name) {
        return Bukkit.createWorld(new WorldCreator(name));
    }
}
