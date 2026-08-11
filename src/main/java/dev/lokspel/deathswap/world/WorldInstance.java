package dev.lokspel.deathswap.world;

import org.bukkit.World;

/**
 * One reusable game world with a lifecycle:
 * {@code FREE -> IN_USE -> RESETTING -> FREE}.
 */
final class WorldInstance {

    enum State {
        FREE, IN_USE, RESETTING
    }

    private final String name;
    private final WorldLoader loader;
    private State state = State.FREE;
    private World world;

    WorldInstance(String name, WorldLoader loader) {
        this.name = name;
        this.loader = loader;
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
            world = loader.load(name);
        }
    }

    void acquire() {
        state = State.IN_USE;
    }

    void markResetting() {
        state = State.RESETTING;
        world = null;
    }

    void reset() {
        state = State.FREE;
    }
}
