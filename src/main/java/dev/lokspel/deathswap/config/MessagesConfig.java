package dev.lokspel.deathswap.config;

import dev.lokspel.deathswap.DeathSwap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class MessagesConfig {

    private static final String PREFIX = "prefix";

    private final FileConfiguration config;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public MessagesConfig(DeathSwap plugin) {
        this.config = load(plugin);
    }

    private FileConfiguration load(DeathSwap plugin) {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public Component get(String key) {
        return parse(config.getString(key, ""));
    }

    public Component get(String key, String placeholder, String value) {
        return parse(
                config.getString(key, ""),
                placeholder,
                value
        );
    }

    public Component prefixed(String key) {
        return parse(replacePrefix(config.getString(key, "")));
    }

    public Component prefixed(String key, String placeholder, String value) {
        return parse(
                replacePrefix(config.getString(key, "")),
                placeholder,
                value
        );
    }

    private String replacePrefix(String text) {
        return text.replace("%prefix%", config.getString(PREFIX, ""));
    }

    private Component parse(String text) {
        return miniMessage.deserialize(text);
    }

    private Component parse(String text, String placeholder, String value) {
        return miniMessage.deserialize(
                text.replace("<" + placeholder + ">", value)
        );
    }
}