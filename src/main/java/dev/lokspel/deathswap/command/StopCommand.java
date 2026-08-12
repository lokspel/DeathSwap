package dev.lokspel.deathswap.command;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.config.MessagesConfig;
import dev.lokspel.deathswap.game.GameManager;
import org.bukkit.command.CommandSender;

public class StopCommand implements SubCommand {

    private final GameManager game;
    private final MessagesConfig messages;

    public StopCommand(DeathSwap plugin) {
        this.game = plugin.getGameManager();
        this.messages = plugin.getMainConfig().messages();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (SubCommand.requirePermission(sender, messages, "deathswap.stop")) return true;
        if (!game.hasActivity()) {
            sender.sendMessage(DeathSwap.getInstance().getMainConfig().messages().prefixed("not-running"));
            return true;
        }
        game.stop();
        return true;
    }
}
