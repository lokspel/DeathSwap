package dev.lokspel.deathswap.commands;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.config.ConfigManager;
import org.bukkit.command.CommandSender;

public class CommandReload implements SubCommand {

    private final ConfigManager config;

    public CommandReload(DeathSwap plugin) {
        this.config = plugin.getConfigManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (SubCommand.requirePermission(sender, "deathswap.reload")) return true;
        config.load();
        sender.sendMessage(config.getMessages().prefixed("config-reloaded"));
        return true;
    }
}
