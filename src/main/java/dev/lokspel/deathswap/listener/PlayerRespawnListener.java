package dev.lokspel.deathswap.listener;

import dev.lokspel.deathswap.DeathSwap;
import dev.lokspel.deathswap.game.GameManager;
import dev.lokspel.deathswap.game.MatchManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    private final GameManager game;

    public PlayerRespawnListener(DeathSwap plugin) {
        this.game = plugin.getGameManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handle(PlayerRespawnEvent event) {
        MatchManager match = game.findMatchByPlayer(event.getPlayer().getUniqueId());
        if (match != null) {
            Location location = match.onPlayerRespawn(event.getPlayer());
            if (location != null) {
                event.setRespawnLocation(location);
            }
        }
    }
}
