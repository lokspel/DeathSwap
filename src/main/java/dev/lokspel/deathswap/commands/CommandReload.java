package dev.lokspel.deathswap.commands;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.config.ConfigManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

public class CommandReload implements SubCommand {

    private final ConfigManager config;
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();

    public CommandReload(DeathSwap plugin) {
        this.config = plugin.getConfigManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("deathswap.reload")) {
            sender.sendMessage(legacySerializer.deserialize("&cNo permission"));
            return true;
        }
        config.load();
        sender.sendMessage(config.getMessages().prefixed("config-reloaded"));
        return true;
    }
}
