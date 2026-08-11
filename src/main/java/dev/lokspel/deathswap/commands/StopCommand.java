package dev.lokspel.deathswap.commands;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.game.GameManager;
import org.bukkit.command.CommandSender;

public class StopCommand implements SubCommand {

    private final GameManager game;

    public StopCommand(DeathSwap plugin) {
        this.game = plugin.getGameManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (SubCommand.requirePermission(sender, "deathswap.stop")) return true;
        if (!game.hasActivity()) {
            sender.sendMessage(DeathSwap.getInstance().getMainConfig().messages().prefixed("not-running"));
            return true;
        }
        game.stop();
        return true;
    }
}
