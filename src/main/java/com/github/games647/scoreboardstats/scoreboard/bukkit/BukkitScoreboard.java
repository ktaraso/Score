package com.github.games647.scoreboardstats.scoreboard.bukkit;

import com.github.games647.scoreboardstats.scoreboard.Group;
import com.github.games647.scoreboardstats.scoreboard.Scoreboard;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class BukkitScoreboard implements Scoreboard {

    private static final String CRITERIA = "dummy";

    private final boolean oldBukkit;
    private final Player player;

    public BukkitScoreboard(Player player, boolean oldBukkit) {
        this.oldBukkit = oldBukkit;
        this.player = player;
    }

    @Override
    public Group getCurrentSidebar() {
        Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        return new BukkitObjective(this, objective, oldBukkit);
    }

    @Override
    public Group getObjective(String uniqueName) {
        Objective objective = player.getScoreboard().getObjective(uniqueName);
        return new BukkitObjective(this, objective, oldBukkit);
    }

    @Override
    public Group addObjective(String uniqueName) {
        Objective newObjective = player.getScoreboard().registerNewObjective(uniqueName, CRITERIA);
        return new BukkitObjective(this, newObjective, oldBukkit);
    }

    @Override
    public Group addObjective(String uniqueName, String displayName) {
        Objective newObjective = player.getScoreboard().registerNewObjective(uniqueName, CRITERIA);
        newObjective.setDisplayName(displayName);
        return new BukkitObjective(this, newObjective, oldBukkit);
    }

    @Override
    public ImmutableSet<Group> getGroups() {
        Set<Objective> objectives = player.getScoreboard().getObjectives();

        ImmutableSet.Builder<Group> builder = ImmutableSet.builder();
        for (Objective objective : objectives) {
            builder.add(new BukkitObjective(this, objective, oldBukkit));
        }

        return builder.build();
    }

    @Override
    public void removeItemGlobal(String name) {
        if (oldBukkit) {
            player.getScoreboard().resetScores(new FastOfflinePlayer(name));
        } else {
            player.getScoreboard().resetScores(name);
        }
    }
}
