package dev.lokspel.deathswap.command;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.config.MessagesConfig;
import dev.lokspel.deathswap.game.GameManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand implements SubCommand {

    private final GameManager game;
    private final MessagesConfig messages;

    public LeaveCommand(DeathSwap plugin) {
        this.game = plugin.getGameManager();
        this.messages = plugin.getMainConfig().messages();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = SubCommand.requirePlayer(sender, messages);
        if (player == null) return true;
        if (!game.isParticipant(player)) {
            sender.sendMessage(DeathSwap.getInstance().getMainConfig().messages().prefixed("not-joined"));
            return true;
        }
        game.leave(player);
        return true;
    }
}
