package dev.lokspel.deathswap.config;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.config.section.MessagesSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {

    private final DeathSwap plugin;
    private MessagesSection messages;
    private int swapInterval;
    private int maxDeaths;
    private int countdownSeconds;
    private int startDelay;
    private int minPlayersToStart;
    private int minPlayersFastStart;
    private int fastStartDelay;
    private boolean pvpEnabled;
    private String countdownTickSound;
    private String countdownGoSound;
    private String swapSound;
    private String winSound;

    public ConfigManager(DeathSwap plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        load();
    }

    public void load() {
        plugin.reloadConfig();
        var cfg = plugin.getConfig();

        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = new MessagesSection(YamlConfiguration.loadConfiguration(messagesFile), "");

        swapInterval = cfg.getInt("game.swap-interval", 180);
        maxDeaths = cfg.getInt("game.max-deaths", 5);
        countdownSeconds = cfg.getInt("game.countdown-seconds", 5);
        startDelay = cfg.getInt("game.start-delay", 10);
        minPlayersToStart = cfg.getInt("game.min-players-to-start", 2);
        minPlayersFastStart = cfg.getInt("game.min-players-fast-start", 4);
        fastStartDelay = cfg.getInt("game.fast-start-delay", 3);
        pvpEnabled = cfg.getBoolean("game.pvp-enabled", true);

        countdownTickSound = cfg.getString("sounds.countdown-tick", "entity.note.pling");
        countdownGoSound = cfg.getString("sounds.countdown-go", "entity.experience_orb.pickup");
        swapSound = cfg.getString("sounds.swap", "entity.enderman.teleport");
        winSound = cfg.getString("sounds.win", "ui.toast.challenge_complete");
    }

    public void setLobbyLocation(Location loc) {
        var cfg = plugin.getConfig();
        cfg.set("lobby.world", loc.getWorld().getName());
        cfg.set("lobby.x", loc.getX());
        cfg.set("lobby.y", loc.getY());
        cfg.set("lobby.z", loc.getZ());
        cfg.set("lobby.yaw", (double) loc.getYaw());
        cfg.set("lobby.pitch", (double) loc.getPitch());
        plugin.saveConfig();
    }

    public Location getLobbyLocation() {
        var cfg = plugin.getConfig();
        if (!cfg.contains("lobby.world")) return null;

        String worldName = cfg.getString("lobby.world");
        if (worldName == null) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        return new Location(world,
            cfg.getDouble("lobby.x"),
            cfg.getDouble("lobby.y"),
            cfg.getDouble("lobby.z"),
            (float) cfg.getDouble("lobby.yaw"),
            (float) cfg.getDouble("lobby.pitch"));
    }

    public int swapInterval() { return swapInterval; }
    public MessagesSection getMessages() { return messages; }
    public int maxDeaths() { return maxDeaths; }
    public int countdownSeconds() { return countdownSeconds; }
    public int startDelay() { return startDelay; }
    public int minPlayersToStart() { return minPlayersToStart; }
    public int minPlayersFastStart() { return minPlayersFastStart; }
    public int fastStartDelay() { return fastStartDelay; }
    public boolean pvpEnabled() { return pvpEnabled; }
    public String countdownTickSound() { return countdownTickSound; }
    public String countdownGoSound() { return countdownGoSound; }
    public String swapSound() { return swapSound; }
    public String winSound() { return winSound; }
}
