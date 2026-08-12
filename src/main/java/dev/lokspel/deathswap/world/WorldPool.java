package dev.lokspel.deathswap.world;

import dev.lokspel.deathswap.DeathSwap;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
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

        int count = Math.max(1, plugin.getMainConfig().worlds().count());
        String prefix = plugin.getMainConfig().worlds().namePrefix();
        boolean dimensions = plugin.getMainConfig().worlds().generateDimensions();
        WorldLoader loader = new WorldLoader();
        for (int i = 0; i < count; i++) {
            instances.add(new WorldInstance(prefix + "_" + i, loader, dimensions));
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

    /**
     * Releases a world back into the pool, resets its chunks asynchronously and
     * reloads it, so it is ready for the next match.
     *
     * <p>The unload/reset is deferred by one tick so that any player teleport
     * out of this world scheduled on the current tick (e.g. teleporting back to
     * the lobby when the match ends) is fully applied before the world is torn
     * down. Unloading a world a player still occupies within the same tick
     * strands them in the void until they rejoin.
     */
    public void deleteWorld(World world) {
        if (world == null) {
            return;
        }

        for (WorldInstance instance : instances) {
            if (!instance.owns(world)) {
                continue;
            }

            if (!plugin.isEnabled()) {
                evacuate(instance);
                reset.clearNow(instance);
                return;
            }

            Bukkit.getGlobalRegionScheduler().run(plugin, _ -> {
                evacuate(instance);

                // Give teleports one tick to complete before unloading the world.
                Bukkit.getGlobalRegionScheduler().runDelayed(
                        plugin,
                        _ -> reset.reset(instance, () -> loadInstance(instance)),
                        1L
                );
            });

            return;
        }
    }

    /**
     * Teleports any player still in any of the instance's worlds back to the
     * lobby, so a world is never torn down while a player occupies it.
     */
    private void evacuate(WorldInstance instance) {
        for (World world : instance.allWorlds()) {
            for (Player player : world.getPlayers()) {
                teleportToLobby(player);
            }
        }
    }

    public void teleportToLobby(Player player) {
        player.teleport(lobbyLocation());
    }

    public Location lobbyLocation() {
        Location lobby = plugin.getMainConfig().lobby().get();
        return Objects.requireNonNullElseGet(lobby,
            () -> Bukkit.getWorlds().getFirst().getSpawnLocation());
    }

    /**
     * Whether the given world is one of the plugin's pooled game worlds.
     */
    public boolean isGameWorld(World world) {
        for (WorldInstance instance : instances) {
            if (instance.owns(world)) return true;
        }
        return false;
    }

    /**
     * Clears all pooled worlds synchronously on shutdown, so the next server
     * start is fresh (no leftover builds from the previous session).
     */
    public void shutdown() {
        for (WorldInstance instance : instances) {
            reset.clearNow(instance);
        }
    }

    /**
     * Finds a free instance whose world is already loaded and spawn
     * pre-generated, so no world is ever created or generated on the server
     * thread.
     */
    private WorldInstance findReadyInstance() {
        List<WorldInstance> ready = new ArrayList<>();
        for (WorldInstance instance : instances) {
            if (instance.isFree() && instance.isLoaded() && instance.isSpawnReady()) {
                ready.add(instance);
            }
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
        applySpawnRule(instance);
        applyBorder(instance);
        preGenerateSpawn(instance);
    }

    /**
     * Applies the configured world border size (in blocks) to every world of
     * the instance, centred on that world's spawn. {@code 0} resets it to the
     * server default.
     */
    private void applyBorder(WorldInstance instance) {
        int size = plugin.getMainConfig().worlds().border();
        for (World world : instance.allWorlds()) {
            WorldBorder border = world.getWorldBorder();
            if (size <= 0) {
                border.reset();
                continue;
            }
            Location spawn = world.getSpawnLocation();
            border.setCenter(spawn.getX(), spawn.getZ());
            border.setSize(size);
        }
    }

    /**
     * Applies the configurable spawn-radius as the per-world
     * {@code respawn_radius} gamerule. It is read back by the match when
     * computing a random respawn point, keeping the radius per world.
     */
    private void applySpawnRule(WorldInstance instance) {
        int radius = Math.max(0, plugin.getMainConfig().worlds().spawnRadius());
        for (World world : instance.allWorlds()) {
            world.setGameRule(GameRules.RESPAWN_RADIUS, radius);
        }
    }

    /**
     * Loads all reusable worlds on the main thread, but staggered one per tick
     * instead of all at once, so the server doesn't spike CPU during startup.
     * Worlds ready before the first match, since the lobby has a long countdown.
     */
    private void warmUp() {
        Iterator<WorldInstance> iterator = instances.iterator();
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (iterator.hasNext()) {
                loadInstance(iterator.next());
            } else {
                task.cancel();
            }
        }, 1L, 1L);
    }

    private void preGenerateSpawn(WorldInstance instance) {
        World world = instance.getWorld();

        int radius = plugin.getMainConfig().worlds().preGenerateRadius();

        Location spawn = world.getSpawnLocation();

        int centerX = spawn.getBlockX() >> 4;
        int centerZ = spawn.getBlockZ() >> 4;

        if (radius <= 0) {
            instance.markSpawnReady();
            return;
        }

        int minX = centerX - radius;
        int minZ = centerZ - radius;
        int maxX = centerX + radius;
        int maxZ = centerZ + radius;

        world.getChunksAtAsync(
                minX,
                minZ,
                maxX,
                maxZ,
                true,
                () -> Bukkit.getGlobalRegionScheduler().run(plugin, _ -> {
                    int x = world.getSpawnLocation().getBlockX();
                    int z = world.getSpawnLocation().getBlockZ();

                    world.setSpawnLocation(
                            x,
                            world.getHighestBlockYAt(x, z) + 1,
                            z
                    );

                    instance.markSpawnReady();
                })
        );
    }
}