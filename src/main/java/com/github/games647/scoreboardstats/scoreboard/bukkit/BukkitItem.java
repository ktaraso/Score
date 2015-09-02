package com.github.games647.scoreboardstats.scoreboard.bukkit;

import com.github.games647.scoreboardstats.scoreboard.Item;
import com.github.games647.scoreboardstats.scoreboard.Group;

import org.bukkit.scoreboard.Score;

public class BukkitItem implements Item {

    private final Score bukkitScore;

    public BukkitItem(Score bukkitScore) {
        this.bukkitScore = bukkitScore;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDisplayName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDisplayName(String newName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean exists() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Group getParent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(Item o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
