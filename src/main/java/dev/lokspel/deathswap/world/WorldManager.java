package dev.lokspel.deathswap.world;

import dev.lokspel.deathswap.DeathSwap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

public class WorldManager {

    private final DeathSwap plugin;

    public WorldManager(DeathSwap plugin) {
        this.plugin = plugin;
    }

    public World createGameWorld() {
        String worldName = "deathswap_" + UUID.randomUUID().toString().substring(0, 8);
        World gameWorld = Bukkit.createWorld(new WorldCreator(worldName));
        if (gameWorld == null) {
            throw new IllegalStateException("Failed to create game world: " + worldName);
        }
        return gameWorld;
    }

    public void deleteWorld(World world) {
        if (world == null) return;

        for (Player player : world.getPlayers()) {
            teleportToLobby(player);
        }

        Path worldFolder = world.getWorldFolder().toPath();
        Bukkit.unloadWorld(world, false);
        deleteDirectory(worldFolder);
    }

    public void teleportToLobby(Player player) {
        Location lobby = plugin.getConfigManager().getLobbyLocation();
        player.teleport(Objects.requireNonNullElseGet(lobby,
            () -> Bukkit.getWorlds().getFirst().getSpawnLocation()));
    }

    private void deleteDirectory(Path path) {
        if (!Files.exists(path)) return;

        try (var files = Files.list(path)) {
            files.forEach(child -> {
                if (Files.isDirectory(child)) {
                    deleteDirectory(child);
                } else {
                    try {
                        Files.delete(child);
                    } catch (IOException e) {
                        plugin.getLogger().warning("Failed to delete: " + child);
                    }
                }
            });
            Files.delete(path);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to delete directory: " + path);
        }
    }
}
