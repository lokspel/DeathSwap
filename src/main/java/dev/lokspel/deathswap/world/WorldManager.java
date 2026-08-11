package dev.lokspel.deathswap.world;

import dev.lokspel.deathswap.DeathSwap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Facade for matches over a pool of reusable worlds. Each world can host one
 * concurrent match; worlds are created asynchronously at startup and their
 * chunks reset when a match ends.
 */
public class WorldManager {

    private final DeathSwap plugin;
    private final WorldResetter resetter;
    private final List<WorldSlot> slots = new ArrayList<>();

    public WorldManager(DeathSwap plugin) {
        this.plugin = plugin;
        this.resetter = new WorldResetter(plugin);

        int count = Math.max(1, plugin.getConfigManager().worldCount());
        for (int i = 0; i < count; i++) {
            slots.add(new WorldSlot("deathswap_" + i));
        }
        warmUp();
    }

    /**
     * Acquires a free reusable world.
     *
     * @return the acquired world, or {@code null} if all worlds are busy
     */
    public World createGameWorld() {
        WorldSlot slot = findReadySlot();
        if (slot == null) return null;

        slot.acquire();
        return slot.getWorld();
    }

    public boolean hasFreeSlot() {
        return findFreeSlot() != null;
    }

    /**
     * Releases a world back into the pool, resets its chunks asynchronously and
     * reloads it, so it is ready for the next match.
     */
    public void deleteWorld(World world) {
        if (world == null) return;

        for (WorldSlot slot : slots) {
            if (!slot.owns(world)) continue;

            for (Player player : world.getPlayers()) {
                teleportToLobby(player);
            }

            Path regionFolder = world.getWorldFolder().toPath().resolve("region");
            slot.release(regionFolder, resetter, () -> loadSlot(slot));
            return;
        }
    }

    public void teleportToLobby(Player player) {
        Location lobby = plugin.getConfigManager().getLobbyLocation();
        player.teleport(Objects.requireNonNullElseGet(lobby,
            () -> Bukkit.getWorlds().getFirst().getSpawnLocation()));
    }

    private WorldSlot findFreeSlot() {
        for (WorldSlot slot : slots) {
            if (slot.isFree()) return slot;
        }
        return null;
    }

    /**
     * Finds a free slot whose world is already loaded and spawn pre-generated,
     * so no world is ever created or generated on the server thread.
     */
    private WorldSlot findReadySlot() {
        List<WorldSlot> ready = new ArrayList<>();
        for (WorldSlot slot : slots) {
            if (slot.isFree() && slot.getWorld() != null) ready.add(slot);
        }
        if (ready.isEmpty()) return null;
        return ready.get(ThreadLocalRandom.current().nextInt(ready.size()));
    }

    /**
     * Loads (or reloads) a world and kicks off asynchronous spawn pre-generation.
     * Must be called on the main thread: {@code Bukkit.createWorld} requires it
     * (Paper fires WorldInitEvent synchronously), but it is cheap because
     * spawn-chunk generation is disabled for our worlds.
     */
    private void loadSlot(WorldSlot slot) {
        slot.load();
        preGenerateSpawn(slot.getWorld());
    }

    /**
     * Creates all reusable worlds gradually on the main thread at startup.
     */
    private void warmUp() {
        for (int i = 0; i < slots.size(); i++) {
            WorldSlot slot = slots.get(i);
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> loadSlot(slot), 20L + i * 40L);
        }
    }

    private void preGenerateSpawn(World world) {
        int radius = plugin.getConfigManager().preGenerateRadius();
        if (radius <= 0) return;

        Location spawn = world.getSpawnLocation();
        int centerX = spawn.getBlockX() >> 4;
        int centerZ = spawn.getBlockZ() >> 4;

        Bukkit.getAsyncScheduler().runNow(plugin, task -> {
            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    world.getChunkAtAsync(x, z).join();
                }
            }
        });
    }
}
