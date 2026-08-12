package dev.lokspel.deathswap.world;

import io.papermc.paper.math.Position;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 * Creates or loads a game world by name and environment. Must be called on the
 * main thread: {@code Bukkit.createWorld} requires it. Modern Paper no longer
 * pre-generates a large spawn area synchronously, so world creation is cheap;
 * the spawn region is pre-generated asynchronously afterwards.
 *
 * <p>Each game world gets its own {@code _nether} and {@code _the_end}
 * companion worlds using the standard Minecraft naming convention, so portals
 * built in a match link to that match's dimensions instead of the server's
 * shared ones.
 *
 * <p>A forced spawn position is set so world creation does not synchronously
 * scan/generate spawn chunks (which would stall the server thread for many
 * seconds per world). The exact position is corrected afterwards in
 * {@link WorldPool#loadInstance}.
 */
final class WorldLoader {

    World load(String name, World.Environment environment) {
        WorldCreator creator = new WorldCreator(name)
                .environment(environment)
                .forcedSpawnPosition(Position.block(0, 64, 0), 0f, 0f);
        return Bukkit.createWorld(creator);
    }
}