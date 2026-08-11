package dev.lokspel.deathswap.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;

public final class MessagesConfig {

    private static final String PREFIX = "prefix";

    private final FileConfiguration config;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public MessagesConfig(FileConfiguration config) {
        this.config = config;
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
        return parse(
                config.getString(PREFIX, "") +
                        config.getString(key, "")
        );
    }

    public Component prefixed(String key, String placeholder, String value) {
        return parse(
                config.getString(PREFIX, "") +
                        config.getString(key, ""),
                placeholder,
                value
        );
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