package dev.lokspel.deathswap.world;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.nio.file.Path;

/**
 * One reusable game world slot with a lifecycle:
 * {@code FREE -> IN_USE -> RESETTING -> FREE}.
 */
final class WorldSlot {

    enum State {
        FREE, IN_USE, RESETTING
    }

    private final String name;
    private State state = State.FREE;
    private World world;

    WorldSlot(String name) {
        this.name = name;
    }

    State state() {
        return state;
    }

    boolean isFree() {
        return state == State.FREE;
    }

    World getWorld() {
        return world;
    }

    boolean owns(World world) {
        return this.world == world;
    }

    void load() {
        if (world == null) {
            world = Bukkit.createWorld(new org.bukkit.WorldCreator(name));
        }
    }

    void acquire() {
        state = State.IN_USE;
    }

    /**
     * Releases the world back into the pool: unloads it, clears its region files
     * off the main thread, then marks the slot free and reloads it.
     *
     * @param regionFolder the folder holding the world's {@code .mca} chunk files
     * @param resetter     clears the region files asynchronously
     * @param onReloaded   run on the main thread after the world is reloaded
     */
    void release(Path regionFolder, WorldResetter resetter, Runnable onReloaded) {
        Bukkit.unloadWorld(world, false);
        state = State.RESETTING;
        world = null;

        resetter.resetRegionAsync(regionFolder, () -> {
            state = State.FREE;
            onReloaded.run();
        });
    }
}
