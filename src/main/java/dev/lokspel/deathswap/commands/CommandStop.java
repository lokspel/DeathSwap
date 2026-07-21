package dev.lokspel.deathswap.commands;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.game.GameManager;
import org.bukkit.command.CommandSender;

public class CommandStop implements SubCommand {

    private final GameManager game;

    public CommandStop(DeathSwap plugin) {
        this.game = plugin.getGameManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (SubCommand.requirePermission(sender, "deathswap.stop")) return true;
        if (!game.hasActivity()) {
            sender.sendMessage(DeathSwap.getInstance().getConfigManager().getMessages().prefixed("not-running"));
            return true;
        }
        game.stop();
        return true;
    }
}
