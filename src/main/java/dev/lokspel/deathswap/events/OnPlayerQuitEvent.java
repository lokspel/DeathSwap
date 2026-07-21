package dev.lokspel.deathswap.events;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.game.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuitEvent implements Listener {

    private final GameManager game;

    public OnPlayerQuitEvent(DeathSwap plugin) {
        this.game = plugin.getGameManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handle(PlayerQuitEvent event) {
        game.leave(event.getPlayer(), false);
    }
}
