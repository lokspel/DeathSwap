package dev.lokspel.deathswap.world;

import dev.lokspel.deathswap.DeathSwap;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Clears a world's region folder (the flat set of {@code .mca} chunk files) off
 * the main thread, then hands the slot back to the pool on the server thread.
 */
final class WorldResetter {

    private final DeathSwap plugin;

    WorldResetter(DeathSwap plugin) {
        this.plugin = plugin;
    }

    void resetRegionAsync(Path regionFolder, Runnable onComplete) {
        CompletableFuture.runAsync(() -> {
            deleteRegionFiles(regionFolder);
            if (onComplete != null) {
                Bukkit.getGlobalRegionScheduler().run(plugin, task -> onComplete.run());
            }
        });
    }

    private void deleteRegionFiles(Path regionFolder) {
        if (!Files.isDirectory(regionFolder)) return;

        try (var files = Files.list(regionFolder)) {
            files.forEach(child -> {
                try {
                    Files.deleteIfExists(child);
                } catch (IOException ignored) {
                }
            });
        } catch (IOException ignored) {
        }
    }
}
