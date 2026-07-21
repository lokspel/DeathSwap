package dev.lokspel.deathswap.commands;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.config.ConfigManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetLobby implements SubCommand {

    private final ConfigManager config;

    public CommandSetLobby(DeathSwap plugin) {
        this.config = plugin.getConfigManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = SubCommand.requirePlayer(sender);
        if (player == null) return true;
        if (SubCommand.requirePermission(sender, "deathswap.setlobby")) return true;
        config.setLobbyLocation(player.getLocation());
        sender.sendMessage(config.getMessages().prefixed("lobby-set"));
        return true;
    }
}
