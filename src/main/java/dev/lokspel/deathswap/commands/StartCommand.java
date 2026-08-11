package dev.lokspel.deathswap.commands;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.game.GameManager;
import org.bukkit.command.CommandSender;

public class StartCommand implements SubCommand {

    private final GameManager game;

    public StartCommand(DeathSwap plugin) {
        this.game = plugin.getGameManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (SubCommand.requirePermission(sender, "deathswap.start")) return true;
        if (!game.forceStart()) {
            sender.sendMessage(DeathSwap.getInstance().getMainConfig().messages().prefixed("not-enough-players"));
        }
        return true;
    }
}
