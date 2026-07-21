package dev.lokspel.deathswap.commands;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.config.section.MessagesSection;
import dev.lokspel.deathswap.game.GameManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

public class CommandStart implements SubCommand {

    private final GameManager game;
    private final MessagesSection messages;

    public CommandStart(DeathSwap plugin) {
        this.game = plugin.getGameManager();
        this.messages = plugin.getConfigManager().getMessages();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("deathswap.start")) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cNo permission"));
            return true;
        }
        if (!game.forceStart()) {
            sender.sendMessage(messages.prefixed("not-enough-players"));
        }
        return true;
    }
}
