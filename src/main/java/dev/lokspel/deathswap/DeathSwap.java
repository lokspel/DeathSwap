package dev.lokspel.deathswap;

import dev.lokspel.deathswap.api.DeathSwapAPI;
import dev.lokspel.deathswap.util.SoftDependUtil;
import dev.lokspel.deathswap.util.placeholderapi.PlayerExpansion;
import dev.lokspel.deathswap.command.CommandDispatcher;
import dev.lokspel.deathswap.command.JoinCommand;
import dev.lokspel.deathswap.command.LeaveCommand;
import dev.lokspel.deathswap.command.ReloadCommand;
import dev.lokspel.deathswap.command.SetLobbyCommand;
import dev.lokspel.deathswap.command.StartCommand;
import dev.lokspel.deathswap.command.StopCommand;
import dev.lokspel.deathswap.command.RegisteredCommand;
import dev.lokspel.deathswap.config.MainConfig;
import dev.lokspel.deathswap.listener.AsyncChatListener;
import dev.lokspel.deathswap.listener.EntityDamageListener;
import dev.lokspel.deathswap.listener.PlayerDeathListener;
import dev.lokspel.deathswap.listener.PlayerQuitListener;
import dev.lokspel.deathswap.listener.PlayerRespawnListener;
import dev.lokspel.deathswap.listener.WorldInitListener;
import dev.lokspel.deathswap.game.GameManager;
import dev.lokspel.deathswap.world.WorldPool;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;

import java.util.List;
import java.util.Objects;

@Getter
public class DeathSwap extends JavaPlugin {

    @Getter
    private static DeathSwap instance;
    private MainConfig mainConfig;
    private WorldPool worldPool;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        instance = this;

        mainConfig = new MainConfig(this);

        new Metrics(this, 33306);

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

        new DeathSwapAPI(this);

        if (SoftDependUtil.PLACEHOLDER_API_ENABLED) {
            new PlayerExpansion(this, "deathswap").register();
            new PlayerExpansion(this, "ds").register();
        }
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.stop();
        }
        if (worldPool != null) {
            worldPool.shutdown();
        }
    }

}
