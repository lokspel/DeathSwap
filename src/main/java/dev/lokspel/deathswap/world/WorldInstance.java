package dev.lokspel.deathswap.world;

import org.bukkit.World;

/**
 * One reusable game world (with its own nether and end companions) with a
 * lifecycle: {@code FREE -> IN_USE -> RESETTING -> FREE}.
 */
final class WorldInstance {

    enum State {
        FREE, IN_USE, RESETTING
    }

    private final String name;
    private final WorldLoader loader;
    private final boolean generateDimensions;
    private State state = State.FREE;
    private World world;
    private World nether;
    private World end;

    WorldInstance(String name, WorldLoader loader, boolean generateDimensions) {
        this.name = name;
        this.loader = loader;
        this.generateDimensions = generateDimensions;
    }

    boolean isFree() {
        return state == State.FREE;
    }

    World getWorld() {
        return world;
    }

    boolean isLoaded() {
        return world != null
            && (!generateDimensions || (nether != null && end != null));
    }

    boolean owns(World world) {
        return world != null && (world == this.world || world == this.nether || world == this.end);
    }

    World[] allWorlds() {
        return generateDimensions
            ? new World[]{world, nether, end}
            : new World[]{world};
    }

    void load() {
        if (world == null) {
            world = loader.load(name, World.Environment.NORMAL);
        }
        if (!generateDimensions) return;

        if (nether == null) {
            nether = loader.load(name + "_nether", World.Environment.NETHER);
        }
        if (end == null) {
            end = loader.load(name + "_the_end", World.Environment.THE_END);
        }
    }

    void acquire() {
        state = State.IN_USE;
    }

    void markResetting() {
        state = State.RESETTING;
        world = null;
        nether = null;
        end = null;
    }

    void reset() {
        state = State.FREE;
    }
}