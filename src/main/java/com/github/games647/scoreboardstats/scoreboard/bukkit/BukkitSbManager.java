package com.github.games647.scoreboardstats.scoreboard.bukkit;

import com.github.games647.scoreboardstats.SbManager;
import com.github.games647.scoreboardstats.ScoreboardStats;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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

    private String expandScore(String scoreName, Objective objective) {
        String cleanScoreName = scoreName;
        final int titleLength = cleanScoreName.length();
        if (titleLength > 16) {
            final Scoreboard scoreboard = objective.getScoreboard();
            //Could maybe cause conflicts but .substring could also make conflicts if they have the same beginning
            final String teamId = String.valueOf(cleanScoreName.hashCode());

            Team team = scoreboard.getTeam(teamId);
            if (team == null) {
                team = scoreboard.registerNewTeam(teamId);
                team.setPrefix(cleanScoreName.substring(0, 16));
                if (titleLength > 32) {
                    //we already validated that this one can only be 48 characters long in the Settings class
                    team.setSuffix(cleanScoreName.substring(32));
                    cleanScoreName = cleanScoreName.substring(16, 32);
                } else {
                    cleanScoreName = cleanScoreName.substring(16);
                }

                team.setDisplayName(cleanScoreName);
                //Bukkit.getOfflinePlayer performance work around
                team.addPlayer(new FastOfflinePlayer(cleanScoreName));
            } else {
                cleanScoreName = team.getDisplayName();
            }
        }

        return cleanScoreName;
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
