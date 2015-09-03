package com.github.games647.scoreboardstats.scoreboard.bukkit;

import com.github.games647.scoreboardstats.SbManager;
import com.github.games647.scoreboardstats.ScoreboardStats;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

/**
 * Managing the scoreboard access.
 *
 * @see com.github.games647.scoreboardstats.protocol.PacketSbManager
 */
public class BukkitSbManager extends SbManager {

    private final boolean oldBukkit;

    /**
     * Initialize scoreboard manager.
     *
     * @param pluginInstance the ScoreboardStats instance
     */
    public BukkitSbManager(ScoreboardStats pluginInstance) {
        super(pluginInstance);

        oldBukkit = isOldScoreboardAPI();
    }

    @Override
    public com.github.games647.scoreboardstats.scoreboard.Scoreboard getOrCreateScoreboard(Player player) {
        return new BukkitScoreboard(player, oldBukkit);
    }

    private boolean isOldScoreboardAPI() {
        try {
            Objective.class.getDeclaredMethod("getScore", String.class);
        } catch (NoSuchMethodException noSuchMethodEx) {
            //since we have an extra class for it (FastOfflinePlayer)
            //we can fail silently
            return true;
        }

        //We have access to the new method
        return false;
    }
}
