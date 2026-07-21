package dev.lokspel.deathswap;

import dev.lokspel.deathswap.commands.CommandDispatcher;
import dev.lokspel.deathswap.commands.CommandJoin;
import dev.lokspel.deathswap.commands.CommandLeave;
import dev.lokspel.deathswap.commands.CommandReload;
import dev.lokspel.deathswap.commands.CommandSetLobby;
import dev.lokspel.deathswap.commands.CommandStart;
import dev.lokspel.deathswap.commands.CommandStop;
import dev.lokspel.deathswap.commands.RegisteredCommand;
import dev.lokspel.deathswap.config.ConfigManager;
import dev.lokspel.deathswap.events.OnAsyncChatEvent;
import dev.lokspel.deathswap.events.OnEntityDamageEvent;
import dev.lokspel.deathswap.events.OnPlayerDeathEvent;
import dev.lokspel.deathswap.events.OnPlayerQuitEvent;
import dev.lokspel.deathswap.events.OnPlayerRespawnEvent;
import dev.lokspel.deathswap.game.GameManager;
import dev.lokspel.deathswap.world.WorldManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public class DeathSwap extends JavaPlugin {

    private static DeathSwap instance;
    private ConfigManager configManager;
    private WorldManager worldManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        worldManager = new WorldManager(this);
        gameManager = new GameManager(this);

        CommandDispatcher dispatcher = new CommandDispatcher(this, List.of(
                new RegisteredCommand("start", new CommandStart(this)),
                new RegisteredCommand("stop", new CommandStop(this)),
                new RegisteredCommand("join", new CommandJoin(this)),
                new RegisteredCommand("leave", new CommandLeave(this)),
                new RegisteredCommand("reload", new CommandReload(this)),
                new RegisteredCommand("setlobby", new CommandSetLobby(this))
        ));
        Objects.requireNonNull(getCommand("deathswap")).setExecutor(dispatcher);

        getServer().getPluginManager().registerEvents(new OnPlayerDeathEvent(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerQuitEvent(), this);
        getServer().getPluginManager().registerEvents(new OnAsyncChatEvent(), this);
        getServer().getPluginManager().registerEvents(new OnEntityDamageEvent(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerRespawnEvent(), this);
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.stop();
        }
    }

    public static DeathSwap getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
