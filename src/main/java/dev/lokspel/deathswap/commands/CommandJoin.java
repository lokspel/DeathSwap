package dev.lokspel.deathswap.commands;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.game.GameManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandJoin implements SubCommand {

    private final GameManager game;

    public CommandJoin(DeathSwap plugin) {
        this.game = plugin.getGameManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = SubCommand.requirePlayer(sender);
        if (player == null) return true;
        game.join(player);
        return true;
    }
}
