package dev.lokspel.deathswap.events;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.game.GameManager;
import dev.lokspel.deathswap.game.MatchManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class OnAsyncChatEvent implements Listener {

    private final GameManager game;

    public OnAsyncChatEvent() {
        this.game = DeathSwap.getInstance().getGameManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handle(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        MatchManager match = game.findMatchByPlayer(sender.getUniqueId());

        if (match != null) {
            event.viewers().clear();
            for (Player p : match.getOnlinePlayers()) {
                event.viewers().add(p);
            }
        } else {
            event.viewers().removeIf(p ->
                p instanceof Player player && game.findMatchByPlayer(player.getUniqueId()) != null);
        }
    }
}
