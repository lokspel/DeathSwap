package dev.lokspel.deathswap.config.section;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;

public class MessagesSection {

    private final FileConfiguration config;
    private final String path;

    public MessagesSection(FileConfiguration config, String path) {
        this.config = config;
        this.path = path;
    }

    public Component message(String key, String... replacements) {
        String msg = config.getString(path + key, "");
        return deserialize(applyReplacements(msg, replacements));
    }

    public Component prefixed(String key, String... replacements) {
        String prefix = config.getString(path + "prefix", "");
        String msg = config.getString(path + key, "");
        return deserialize(prefix + applyReplacements(msg, replacements));
    }

    public String scoreboardEntry(String playerName) {
        String template = config.getString(path + "scoreboard-entry", "<white>{player}</white>");
        String escaped = template.replace("{player}", "\u0000");
        Component component = deserialize(escaped);
        String legacy = LegacyComponentSerializer.legacySection().serialize(component);
        return legacy.replace("\u0000", playerName);
    }

    private Component deserialize(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }

    private String applyReplacements(String text, String... replacements) {
        if (replacements.length == 0) return text;
        String result = text;
        for (int i = 0; i < replacements.length - 1; i += 2) {
            result = result.replace("<" + replacements[i] + ">", replacements[i + 1]);
        }
        return result;
    }
}
