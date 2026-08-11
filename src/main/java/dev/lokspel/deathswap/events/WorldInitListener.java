package dev.lokspel.deathswap.events;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

/**
 * Skips the expensive synchronous spawn-chunk generation when a game world is
 * created or reloaded. Spawn area is pre-generated asynchronously by the
 * WorldManager afterwards, keeping {@code Bukkit.createWorld} near-instant.
 */
public class WorldInitListener implements Listener {

    @EventHandler
    public void handle(WorldInitEvent event) {
        World world = event.getWorld();
        if (world.getName().startsWith("deathswap_")) {
            world.setKeepSpawnInMemory(false);
            world.setAutoSave(false);
        }
    }
}
