package dev.lokspel.deathswap.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface SubCommand {

    boolean execute(CommandSender sender, String[] args);

    static Component color(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    static boolean requirePermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) return false;
        sender.sendMessage(color("&cNo permission"));
        return true;
    }

    static Player requirePlayer(CommandSender sender) {
        if (sender instanceof Player player) return player;
        sender.sendMessage(color("&cOnly players can use this command"));
        return null;
    }
}
