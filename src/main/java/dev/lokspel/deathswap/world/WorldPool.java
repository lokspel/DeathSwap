package dev.lokspel.deathswap.world;

import dev.lokspel.deathswap.DeathSwap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Facade over a pool of reusable game worlds. Each world can host one
 * concurrent match; worlds are created at startup and their chunks reset when a
 * match ends.
 */
public class WorldPool {

    private final DeathSwap plugin;
    private final WorldReset reset;
    private final List<WorldInstance> instances = new ArrayList<>();

    public WorldPool(DeathSwap plugin) {
        this.plugin = plugin;
        this.reset = new WorldReset(plugin);

        int count = Math.max(1, plugin.getConfigManager().worldCount());
        WorldLoader loader = new WorldLoader();
        for (int i = 0; i < count; i++) {
            instances.add(new WorldInstance("deathswap_" + i, loader));
        }
        warmUp();
    }

    /**
     * Acquires a free reusable world.
     *
     * @return the acquired world, or {@code null} if all worlds are busy
     */
    public World createGameWorld() {
        WorldInstance instance = findReadyInstance();
        if (instance == null) return null;

        instance.acquire();
        return instance.getWorld();
    }

    public boolean hasFreeInstance() {
        return findFreeInstance() != null;
    }

    /**
     * Releases a world back into the pool, resets its chunks asynchronously and
     * reloads it, so it is ready for the next match.
     */
    public void deleteWorld(World world) {
        if (world == null) return;

        for (WorldInstance instance : instances) {
            if (!instance.owns(world)) continue;

            for (Player player : world.getPlayers()) {
                teleportToLobby(player);
            }

            reset.reset(instance, () -> loadInstance(instance));
            return;
        }
    }

    public void teleportToLobby(Player player) {
        Location lobby = plugin.getConfigManager().getLobbyLocation();
        player.teleport(Objects.requireNonNullElseGet(lobby,
            () -> Bukkit.getWorlds().getFirst().getSpawnLocation()));
    }

    private WorldInstance findFreeInstance() {
        for (WorldInstance instance : instances) {
            if (instance.isFree()) return instance;
        }
        return null;
    }

    /**
     * Finds a free instance whose world is already loaded and spawn
     * pre-generated, so no world is ever created or generated on the server
     * thread.
     */
    private WorldInstance findReadyInstance() {
        List<WorldInstance> ready = new ArrayList<>();
        for (WorldInstance instance : instances) {
            if (instance.isFree() && instance.getWorld() != null) ready.add(instance);
        }
        if (ready.isEmpty()) return null;
        return ready.get(ThreadLocalRandom.current().nextInt(ready.size()));
    }

    /**
     * Loads (or reloads) a world and kicks off asynchronous spawn
     * pre-generation.
     */
    private void loadInstance(WorldInstance instance) {
        instance.load();
        preGenerateSpawn(instance.getWorld());
    }

    /**
     * Creates all reusable worlds on the main thread at startup. Cheap because
     * spawn-chunk generation is disabled for our worlds.
     */
    private void warmUp() {
        for (WorldInstance instance : instances) {
            loadInstance(instance);
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
