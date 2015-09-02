package com.github.games647.scoreboardstats.scoreboard;

import com.github.games647.scoreboardstats.SbManager;

import org.bukkit.entity.Player;

/**
 * Scheduled appear task
 */
public class DelayedShowTask implements Runnable {

    private final SbManager scoreboardManager;

    private final Player player;
    private final boolean showTop;

    /**
     * Creates a new scheduled appear of the normal scoreboard or the temp scoreboard
     *
     * @param player the specific player
     * @param showTop if the temp scoreboard be displayed
     * @param scoreboardManager the associated sbManager
     */
    public DelayedShowTask(Player player, boolean showTop, SbManager scoreboardManager) {
        this.scoreboardManager = scoreboardManager;
        this.player = player;
        this.showTop = showTop;
    }

    @Override
    public void run() {
        if (showTop) {
            scoreboardManager.createTopListScoreboard(player);
        } else {
            scoreboardManager.createScoreboard(player);
        }
    }
}
