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

    public Component get(String key, String p1, String v1, String p2, String v2) {
        return miniMessage.deserialize(
                config.getString(key, "").replace("<" + p1 + ">", v1).replace("<" + p2 + ">", v2)
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