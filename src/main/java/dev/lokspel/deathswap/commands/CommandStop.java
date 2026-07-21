package dev.lokspel.deathswap.commands;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.config.section.MessagesSection;
import dev.lokspel.deathswap.game.GameManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

public class CommandStop implements SubCommand {

    private final GameManager game;
    private final MessagesSection messages;
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();

    public CommandStop(DeathSwap plugin) {
        this.game = plugin.getGameManager();
        this.messages = plugin.getConfigManager().getMessages();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("deathswap.stop")) {
            sender.sendMessage(legacySerializer.deserialize("&cNo permission"));
            return true;
        }
        if (!game.hasActivity()) {
            sender.sendMessage(messages.prefixed("not-running"));
            return true;
        }
        game.stop();
        return true;
    }
}
