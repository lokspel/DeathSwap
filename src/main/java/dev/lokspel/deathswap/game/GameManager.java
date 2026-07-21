package dev.lokspel.deathswap.game;

import dev.lokspel.deathswap.DeathSwap;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GameManager {

    private final DeathSwap plugin;
    private final LobbyManager lobby;
    private final Map<UUID, MatchManager> matches = new HashMap<>();

    public GameManager(DeathSwap plugin) {
        this.plugin = plugin;
        this.lobby = new LobbyManager(plugin);
    }

    public void join(Player player) {
        UUID uuid = player.getUniqueId();

        if (findMatchByPlayer(uuid) != null || lobby.contains(uuid)) {
            player.sendMessage(plugin.getConfigManager().getMessages().prefixed("already-joined"));
            return;
        }

        lobby.join(player);
        lobby.tryAutoStart(this::createMatch);
    }

    public void leave(Player player) {
        leave(player, true);
    }

    public void leave(Player player, boolean teleport) {
        UUID uuid = player.getUniqueId();
        MatchManager match = findMatchByPlayer(uuid);

        if (match != null) {
            match.leave(player, teleport);
        } else if (lobby.contains(uuid)) {
            lobby.leave(uuid);
        } else {
            return;
        }

        player.sendMessage(plugin.getConfigManager().getMessages().prefixed("left"));
    }

    public void onPlayerDeath(Player player) {
        MatchManager match = findMatchByPlayer(player.getUniqueId());
        if (match != null) {
            match.onPlayerDeath(player);
        }
    }

    public boolean forceStart() {
        if (lobby.size() < 2) return false;

        lobby.cancelTask();
        createMatch();
        return true;
    }

    private void createMatch() {
        Set<Player> players = lobby.getOnlinePlayers();
        if (players.size() < 2) return;

        lobby.clear();

        UUID matchId = UUID.randomUUID();
        matches.put(matchId, new MatchManager(plugin, new ArrayList<>(players), () -> matches.remove(matchId)));
    }

    public void stop() {
        for (MatchManager m : List.copyOf(matches.values())) {
            m.stop();
        }
        matches.clear();
        lobby.clear();
    }

    public MatchManager findMatchByPlayer(UUID uuid) {
        for (MatchManager m : matches.values()) {
            if (m.contains(uuid)) return m;
        }
        return null;
    }

    public boolean isPlayer(Player player) {
        return findMatchByPlayer(player.getUniqueId()) != null || lobby.contains(player.getUniqueId());
    }

    public boolean hasActivity() {
        return !matches.isEmpty() || lobby.size() > 0;
    }
}
