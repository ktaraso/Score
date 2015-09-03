package com.github.games647.scoreboardstats.scoreboard.bukkit;

import com.github.games647.scoreboardstats.scoreboard.Item;
import com.github.games647.scoreboardstats.scoreboard.Group;
import com.google.common.primitives.Ints;

import org.bukkit.scoreboard.Score;

public class BukkitItem implements Item {

    private final Score bukkitScore;
    private final boolean oldBukkit;

    public BukkitItem(Score bukkitScore, boolean oldBukkit) {
        this.bukkitScore = bukkitScore;
        this.oldBukkit = oldBukkit;
    }

    @Override
    public int getScore() {
        return bukkitScore.getScore();
    }

    @Override
    public void setScore(int newScore) {
        bukkitScore.setScore(newScore);
    }

    @Override
    public String getUniqueId() {
        throw new UnsupportedOperationException("Bukkit API has it's limitations");
    }

    @Override
    public String getDisplayName() {
        if (oldBukkit) {
            return bukkitScore.getPlayer().getName();
        } else {
            return bukkitScore.getEntry();
        }
    }

    @Override
    public void setDisplayName(String newName) {
        throw new UnsupportedOperationException("Bukkit API has it's limitations");
    }

    @Override
    public boolean exists() {
        throw new UnsupportedOperationException("Bukkit API has it's limitations");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Bukkit API has limitations");
    }

    @Override
    public Group getParent() {
        throw new UnsupportedOperationException("Bukkit API has too many limitations");
    }

    @Override
    public int compareTo(Item other) {
        //Reverse order - first the highest element like the scoreboard ingame
        return Ints.compare(other.getScore(), bukkitScore.getScore());
    }
}
