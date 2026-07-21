package dev.lokspel.deathswap.commands;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.config.ConfigManager;
import dev.lokspel.deathswap.config.section.MessagesSection;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetLobby implements SubCommand {

    private final ConfigManager config;
    private final MessagesSection messages;
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();

    public CommandSetLobby(DeathSwap plugin) {
        this.config = plugin.getConfigManager();
        this.messages = plugin.getConfigManager().getMessages();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(legacySerializer.deserialize("&cOnly players can use this command"));
            return true;
        }
        if (!sender.hasPermission("deathswap.setlobby")) {
            sender.sendMessage(legacySerializer.deserialize("&cNo permission"));
            return true;
        }
        config.setLobbyLocation(player.getLocation());
        sender.sendMessage(messages.prefixed("lobby-set"));
        return true;
    }
}
