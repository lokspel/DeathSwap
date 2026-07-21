package dev.lokspel.deathswap.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DeathManager {

    private final Map<UUID, Integer> counts = new HashMap<>();

    public void init(Set<UUID> players) {
        counts.clear();
        players.forEach(uuid -> counts.put(uuid, 0));
    }

    public int add(UUID uuid) {
        return counts.merge(uuid, 1, Integer::sum);
    }

    public boolean contains(UUID uuid) {
        return counts.containsKey(uuid);
    }

    public void remove(UUID uuid) {
        counts.remove(uuid);
    }

    public Set<Player> getAlivePlayers() {
        Set<Player> alive = new HashSet<>();
        for (UUID uuid : counts.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline() && player.getGameMode() != GameMode.SPECTATOR) {
                alive.add(player);
            }
        }
        return alive;
    }

    public Map<UUID, Integer> getAll() {
        return new HashMap<>(counts);
    }

    public int size() {
        return counts.size();
    }

    public void clear() {
        counts.clear();
    }
}
