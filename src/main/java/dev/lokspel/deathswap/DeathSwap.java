package dev.lokspel.deathswap;

import dev.lokspel.deathswap.commands.CommandDispatcher;
import dev.lokspel.deathswap.commands.JoinCommand;
import dev.lokspel.deathswap.commands.LeaveCommand;
import dev.lokspel.deathswap.commands.ReloadCommand;
import dev.lokspel.deathswap.commands.SetLobbyCommand;
import dev.lokspel.deathswap.commands.StartCommand;
import dev.lokspel.deathswap.commands.StopCommand;
import dev.lokspel.deathswap.commands.RegisteredCommand;
import dev.lokspel.deathswap.config.ConfigManager;
import dev.lokspel.deathswap.events.AsyncChatListener;
import dev.lokspel.deathswap.events.EntityDamageListener;
import dev.lokspel.deathswap.events.PlayerDeathListener;
import dev.lokspel.deathswap.events.PlayerQuitListener;
import dev.lokspel.deathswap.events.PlayerRespawnListener;
import dev.lokspel.deathswap.events.WorldInitListener;
import dev.lokspel.deathswap.game.GameManager;
import dev.lokspel.deathswap.world.WorldPool;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public class DeathSwap extends JavaPlugin {

    private static DeathSwap instance;
    private ConfigManager configManager;
    private WorldPool worldPool;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);

        getServer().getPluginManager().registerEvents(new WorldInitListener(), this);
        worldPool = new WorldPool(this);
        gameManager = new GameManager(this);

        CommandDispatcher dispatcher = new CommandDispatcher(this, List.of(
                new RegisteredCommand("start", new StartCommand(this)),
                new RegisteredCommand("stop", new StopCommand(this)),
                new RegisteredCommand("join", new JoinCommand(this)),
                new RegisteredCommand("leave", new LeaveCommand(this)),
                new RegisteredCommand("reload", new ReloadCommand(this)),
                new RegisteredCommand("setlobby", new SetLobbyCommand(this))
        ));
        Objects.requireNonNull(getCommand("deathswap")).setExecutor(dispatcher);

        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new AsyncChatListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
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

    public WorldPool getWorldPool() {
        return worldPool;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
