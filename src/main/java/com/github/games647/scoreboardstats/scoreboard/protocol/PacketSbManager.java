package com.github.games647.scoreboardstats.scoreboard.protocol;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.games647.scoreboardstats.SbManager;
import com.github.games647.scoreboardstats.ScoreboardStats;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;

/**
 * Manage the scoreboards with packet-use
 */
public class PacketSbManager extends SbManager {

    private final Map<Player, PacketScoreboard> scoreboards = new WeakHashMap<Player, PacketScoreboard>(50);

    /**
     * Creates a new scoreboard manager for the packet system.
     *
     * @param plugin ScoreboardStats instance
     */
    public PacketSbManager(ScoreboardStats plugin) {
        super(plugin);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketListener(plugin, this));
    }

    /**
     * Gets the scoreboard from a player.
     *
     * @param player who owns the scoreboard
     * @return the scoreboard instance
     */
    @Override
    public PacketScoreboard getOrCreateScoreboard(Player player) {
        PacketScoreboard scoreboard = scoreboards.get(player);
        if (scoreboard == null) {
            //lazy loading due potenially performance issues
            scoreboard = new PacketScoreboard(player);
            scoreboards.put(player, scoreboard);
        }

        return scoreboard;
    }

    @Override
    public void unregisterAll() {
        super.unregisterAll();

        scoreboards.clear();
    }
}
