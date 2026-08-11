package dev.lokspel.deathswap.events;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.game.GameManager;
import dev.lokspel.deathswap.game.MatchManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AsyncChatListener implements Listener {

    private final GameManager game;
    private final DeathSwap plugin;

    public AsyncChatListener(DeathSwap plugin) {
        this.plugin = plugin;
        this.game = plugin.getGameManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handle(AsyncChatEvent event) {
        if (!plugin.getConfigManager().isolateChat()) {
            return;
        }

        Player sender = event.getPlayer();
        MatchManager match = game.findMatchByPlayer(sender.getUniqueId());

        if (match != null) {
            event.viewers().retainAll(match.getOnlinePlayers());
            return;
        }

        event.viewers().removeIf(viewer ->
                viewer instanceof Player player &&
                        game.findMatchByPlayer(player.getUniqueId()) != null
        );
    }
}