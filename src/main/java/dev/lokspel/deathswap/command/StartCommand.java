package dev.lokspel.deathswap.command;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.config.MessagesConfig;
import dev.lokspel.deathswap.game.GameManager;
import org.bukkit.command.CommandSender;

public class StartCommand implements SubCommand {

    private final GameManager game;
    private final MessagesConfig messages;

    public StartCommand(DeathSwap plugin) {
        this.game = plugin.getGameManager();
        this.messages = plugin.getMainConfig().messages();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("deathswap.start")) {
            sender.sendMessage(messages.get("no-permission"));
            return true;
        }
        if (!game.forceStart()) {
            sender.sendMessage(messages.prefixed("not-enough-players"));
        }
        return true;
    }
}
