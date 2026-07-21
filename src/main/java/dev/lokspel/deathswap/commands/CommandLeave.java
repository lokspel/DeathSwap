package dev.lokspel.deathswap.commands;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.config.section.MessagesSection;
import dev.lokspel.deathswap.game.GameManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLeave implements SubCommand {

    private final GameManager game;
    private final MessagesSection messages;
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();

    public CommandLeave(DeathSwap plugin) {
        this.game = plugin.getGameManager();
        this.messages = plugin.getConfigManager().getMessages();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(legacySerializer.deserialize("&cOnly players can use this command"));
            return true;
        }
        if (!game.isPlayer(player)) {
            sender.sendMessage(messages.prefixed("not-joined"));
            return true;
        }
        game.leave(player);
        return true;
    }
}
