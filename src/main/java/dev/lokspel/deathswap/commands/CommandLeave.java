package dev.lokspel.deathswap.commands;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.game.GameManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLeave implements SubCommand {

    private final GameManager game;

    public CommandLeave(DeathSwap plugin) {
        this.game = plugin.getGameManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = SubCommand.requirePlayer(sender);
        if (player == null) return true;
        if (!game.isParticipant(player)) {
            sender.sendMessage(DeathSwap.getInstance().getConfigManager().getMessages().prefixed("not-joined"));
            return true;
        }
        game.leave(player);
        return true;
    }
}
