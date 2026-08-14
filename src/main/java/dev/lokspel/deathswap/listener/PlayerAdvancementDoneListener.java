package dev.lokspel.deathswap.listener;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.game.GameManager;
import dev.lokspel.deathswap.game.MatchManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerAdvancementDoneListener implements Listener {

    private final DeathSwap plugin;
    private final GameManager game;

    public PlayerAdvancementDoneListener(DeathSwap plugin) {
        this.plugin = plugin;
        this.game = plugin.getGameManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handle(PlayerAdvancementDoneEvent event) {
        if (!plugin.getMainConfig().hide().isolateAchievements()) {
            return;
        }

        Player player = event.getPlayer();
        MatchManager match = game.findMatchByPlayer(player.getUniqueId());
        if (match == null) {
            return;
        }

        Component message = event.message();
        if (message == null) {
            return;
        }

        event.message(null);

        List<Player> recipients = new ArrayList<>(match.getOnlinePlayers());
        recipients.forEach(p -> p.sendMessage(message));
    }
}
