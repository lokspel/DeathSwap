package dev.lokspel.deathswap.api;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.config.MainConfig;
import dev.lokspel.deathswap.game.GameManager;
import dev.lokspel.deathswap.world.WorldPool;

public class DeathSwapAPI {

    private static DeathSwapAPI instance;
    private final DeathSwap plugin;

    public DeathSwapAPI(DeathSwap plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public static DeathSwapAPI getInstance() {
        return instance;
    }

    public DeathSwap getPlugin() {
        return plugin;
    }

    public GameManager getGameManager() {
        return plugin.getGameManager();
    }

    public MainConfig getMainConfig() {
        return plugin.getMainConfig();
    }

    public WorldPool getWorldPool() {
        return plugin.getWorldPool();
    }
}