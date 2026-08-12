package dev.lokspel.deathswap.command;

import dev.lokspel.deathswap.config.MessagesConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface SubCommand {

    boolean execute(CommandSender sender, String[] args);

    static boolean requirePermission(CommandSender sender, MessagesConfig messages, String permission) {
        if (sender.hasPermission(permission)) return false;
        sender.sendMessage(messages.get("no-permission"));
        return true;
    }

    static Player requirePlayer(CommandSender sender, MessagesConfig messages) {
        if (sender instanceof Player player) return player;
        sender.sendMessage(messages.get("player-only"));
        return null;
    }
}
