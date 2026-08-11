package dev.lokspel.deathswap.commands;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.config.MainConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLobbyCommand implements SubCommand {

    private final MainConfig config;

    public SetLobbyCommand(DeathSwap plugin) {
        this.config = plugin.getMainConfig();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = SubCommand.requirePlayer(sender);
        if (player == null) return true;
        if (SubCommand.requirePermission(sender, "deathswap.setlobby")) return true;
        config.lobby().set(player.getLocation());
        sender.sendMessage(config.messages().prefixed("lobby-set"));
        return true;
    }
}
