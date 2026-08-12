package dev.lokspel.deathswap.command;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.config.MainConfig;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements SubCommand {

    private final MainConfig config;

    public ReloadCommand(DeathSwap plugin) {
        this.config = plugin.getMainConfig();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (SubCommand.requirePermission(sender, config.messages(), "deathswap.reload")) return true;
        config.load();
        sender.sendMessage(config.messages().prefixed("config-reloaded"));
        return true;
    }
}
