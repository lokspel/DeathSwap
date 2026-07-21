package dev.lokspel.deathswap.events;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.game.GameManager;
import dev.lokspel.deathswap.game.MatchManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class OnPlayerRespawnEvent implements Listener {

    private final GameManager game;

    public OnPlayerRespawnEvent() {
        this.game = DeathSwap.getInstance().getGameManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handle(PlayerRespawnEvent event) {
        MatchManager match = game.findMatchByPlayer(event.getPlayer().getUniqueId());
        if (match != null) {
            match.onPlayerRespawn(event.getPlayer());
        }
    }
}
