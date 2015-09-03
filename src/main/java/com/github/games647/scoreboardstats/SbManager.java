package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.config.Lang;
import com.github.games647.scoreboardstats.config.Settings;
import com.github.games647.scoreboardstats.scoreboard.DelayedShowTask;
import com.github.games647.scoreboardstats.scoreboard.Item;
import com.github.games647.scoreboardstats.scoreboard.Group;
import com.github.games647.scoreboardstats.scoreboard.Scoreboard;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.github.games647.scoreboardstats.variables.UnknownVariableException;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Manage the scoreboard access.
 */
public abstract class SbManager {

    protected static final String SB_NAME = "Stats";
    protected static final String TEMP_SB_NAME = SB_NAME + 'T';

    private static final int MAX_ITEM_LENGTH = 16;

    protected final ScoreboardStats plugin;
    protected final ReplaceManager replaceManager;

    private final String permission;

    public SbManager(ScoreboardStats plugin) {
        this.plugin = plugin;
        this.replaceManager = new ReplaceManager(this, plugin);

        this.permission = plugin.getName().toLowerCase() + ".use";
    }

    public abstract Scoreboard getOrCreateScoreboard(Player player);

    /**
     * Get the replace manager.
     *
     * Warning: The replace manger is bounded to this scoreboard manager.
     * Every time the built-in reload command is executed it will create also
     * a new instance of replace manager.
     *
     * @return the replace manager
     * @deprecated Use ScoreboardStats.getReplaceManager
     */
    @Deprecated
    public ReplaceManager getReplaceManager() {
        return replaceManager;
    }

    /**
     * Creates a new scoreboard based on the configuration.
     *
     * @param player for who should the scoreboard be set.
     */
    public void createScoreboard(Player player) {
        Scoreboard scoreboard = getOrCreateScoreboard(player);
        final Group oldObjective = scoreboard.getCurrentSidebar();
        if (!isValid(player) || oldObjective != null && !TEMP_SB_NAME.equals(oldObjective.getUniqueId())) {
            //Check if another scoreboard is showing
            return;
        }

        //Creates a new personal scoreboard and a new objective
        Group objective = scoreboard.addObjective(SB_NAME, Settings.getTitle());

        sendConstantItems(objective);
        sendUpdate(player, true);

        //Schedule the next tempscoreboard show
        scheduleShowTask(player, true);
    }

    public void createTopListScoreboard(Player player) {
        Scoreboard scoreboard = getOrCreateScoreboard(player);
        final Group oldObjective = scoreboard.getCurrentSidebar();
        if (!isValid(player) || oldObjective == null
                || !oldObjective.getUniqueId().startsWith(SB_NAME)) {
            //Check if another scoreboard is showing
            return;
        }

        //remove old scores
        if (TEMP_SB_NAME.equals(oldObjective.getUniqueId())) {
            oldObjective.remove();
        }

        final Group objective = scoreboard.addObjective(TEMP_SB_NAME, Settings.getTempTitle());

        //Colorize and send all elements
        for (Map.Entry<String, Integer> entry : plugin.getStatsDatabase().getTop()) {
            final String scoreName = stripLength(Settings.getTempColor() + entry.getKey());
            sendScore(objective, scoreName, scoreName, entry.getValue());
        }

        //schedule the next normal scoreboard show
        scheduleShowTask(player, false);
    }

    public void onUpdate(Player player) {
        final Group objective = getOrCreateScoreboard(player).getCurrentSidebar();
        if (objective == null) {
            //The player has no scoreboard so create one
            createScoreboard(player);
        } else {
            sendUpdate(player, false);
        }
    }

    public void update(Player player, String itemName, int newScore) {
        final Scoreboard scoreboard = getOrCreateScoreboard(player);
        if (scoreboard != null) {
            final Group objective = scoreboard.getObjective(SB_NAME);
            if (objective != null) {
                sendScore(objective, itemName, itemName, newScore);
            }
        }
    }

    /**
     * Adding all players to the refresh queue and loading the player stats if enabled
     */
    public void registerAll() {
        final boolean ispvpstats = Settings.isPvpStats();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOnline()) {
                if (ispvpstats) {
                    //maybe batch this
                    player.removeMetadata("player_stats", plugin);
                    plugin.getStatsDatabase().loadAccountAsync(player);
                }

                plugin.getRefreshTask().addToQueue(player);
            }
        }
    }

    /**
     * Clear the scoreboard for all players
     */
    public void unregisterAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            unregister(player);
        }
    }

    /**
     * Unregister ScoreboardStats from the player
     *
     * @param player who owns the scoreboard
     */
    public void unregister(Player player) {
        if (player.isOnline()) {
            for (Group objective : getOrCreateScoreboard(player).getGroups()) {
                final String objectiveName = objective.getUniqueId();
                if (objectiveName.startsWith(SB_NAME)) {
                    objective.remove();
                }
            }
        }
    }

    /**
     * Called if the scoreboard should be updated.
     *
     * @param player for who should the scoreboard be set.
     * @param complete if the scoreboard was just created
     */
    protected void sendUpdate(Player player, boolean complete) {
        final Group objective = getOrCreateScoreboard(player).getCurrentSidebar();
        //don't override other scoreboards
        if (objective != null && SB_NAME.equals(objective.getUniqueId())) {
            sendScoreVariable(objective, player, complete);
            sendTextVariable(objective, player, complete);
        }
    }

    private void sendConstantItems(Group objective) {
        for (Map.Entry<String, Integer> entry : Settings.getConstantItems()) {
            final String title = entry.getKey();
            final int score = entry.getValue();

            sendScore(objective, title, title, score);
        }
    }

    private void sendTextVariable(Group objective, Player player, boolean complete) {
        final Iterator<Map.Entry<String, String>> iter = Settings.getTextItems();
        while (iter.hasNext()) {
            final Map.Entry<String, String> entry = iter.next();
            final String variable = entry.getKey();
            final String title = entry.getValue();

            final Item item = objective.getItem(title);
            try {
                String displayName = title;
                int oldScore = 0;
                if (item != null) {
                    displayName = title;
                    oldScore = item.getScore();
                }

                final ReplaceEvent replaceEvent = replaceManager.getScore(player, variable, true, displayName
                        , oldScore, complete);
                if (replaceEvent.isModified()) {
                    sendScore(objective, title, replaceEvent.getDisplayText(), replaceEvent.getScore());
                }
            } catch (UnknownVariableException ex) {
                //Remove the variable becaue we can't replace it
                iter.remove();
                plugin.getLogger().info(Lang.get("unknownVariable", variable));
            }
        }
    }

    private void sendScoreVariable(Group objective, Player player, boolean complete) {
        final Iterator<Map.Entry<String, String>> iter = Settings.getItems();
        while (iter.hasNext()) {
            final Map.Entry<String, String> entry = iter.next();
            final String title = entry.getKey();
            final String variable = entry.getValue();

            final Item item = objective.getItem(title);
            try {
                String displayName = title;
                int oldScore = 0;
                if (item != null) {
                    displayName = title;
                    oldScore = item.getScore();
                }

                final ReplaceEvent replaceEvent = replaceManager.getScore(player, variable, false, displayName
                        , oldScore, complete);
                if (replaceEvent.isModified()) {
                    sendScore(objective, title, replaceEvent.getDisplayText(), replaceEvent.getScore());
                }
            } catch (UnknownVariableException ex) {
                //Remove the variable becaue we can't replace it
                iter.remove();
                plugin.getLogger().info(Lang.get("unknownVariable", variable));
            }
        }
    }

    protected void scheduleShowTask(Player player, boolean action) {
        if (!Settings.isTempScoreboard()) {
            return;
        }

        int intervall;
        if (action) {
            intervall = Settings.getTempAppear();
        } else {
            intervall = Settings.getTempDisappear();
        }

        Bukkit.getScheduler().runTaskLater(plugin, new DelayedShowTask(player, action, this), intervall * 20L);
    }

    protected String stripLength(String check) {
        if (check.length() > MAX_ITEM_LENGTH) {
            return check.substring(0, MAX_ITEM_LENGTH);
        }

        return check;
    }

    protected boolean isValid(Player player) {
        return player.hasPermission(permission) && player.isOnline()
                && Settings.isActiveWorld(player.getWorld().getName());
    }

    private void sendScore(Group objective, String title, String newTitle, int value) {
        Item score = objective.getItem(title);
        if (score == null) {
            score = objective.addItem(title, value);
        } else {
            score.setScore(value);
        }

        score.setDisplayName(newTitle);
    }
}
