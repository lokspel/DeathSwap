package dev.lokspel.deathswap.events;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.game.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class OnPlayerDeathEvent implements Listener {

    private final GameManager game;

    public OnPlayerDeathEvent(DeathSwap plugin) {
        this.game = plugin.getGameManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handle(PlayerDeathEvent event) {
        game.onPlayerDeath(event.getPlayer());
    }
}
