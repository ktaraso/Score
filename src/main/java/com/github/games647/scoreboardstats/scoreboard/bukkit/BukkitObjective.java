package com.github.games647.scoreboardstats.scoreboard.bukkit;

import com.github.games647.scoreboardstats.scoreboard.Item;
import com.github.games647.scoreboardstats.scoreboard.Group;

import java.util.List;
import java.util.Map;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class BukkitObjective implements Group {

    private final BukkitScoreboard scoreboard;

    private final Objective objective;
    private final boolean oldBukkit;

    public BukkitObjective(BukkitScoreboard scoreboard, Objective objective, boolean oldBukkit) {
        this.scoreboard = scoreboard;
        this.objective = objective;

        this.oldBukkit = oldBukkit;
    }

    @Override
    public void addItems(Map<String, Integer> newItems) {
        for (Map.Entry<String, Integer> entrySet : newItems.entrySet()) {
            addItem(entrySet.getKey(), entrySet.getValue());
        }
    }

    @Override
    public BukkitItem addItem(String... text) {
        BukkitItem item = null;
        for (String name : text) {
            item = addItem(name, 0);
        }

        return item;
    }

    @Override
    public BukkitItem addItem(String text, int score) {
        throw new UnsupportedOperationException("Bukkit API has it's limitations");
    }

    @Override
    public BukkitItem getItem(String name) {
        if (oldBukkit) {
            return new BukkitItem(objective.getScore(new FastOfflinePlayer(name)), oldBukkit);
        }

        return new BukkitItem(objective.getScore(name), oldBukkit);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Bukkit API has it's limitations");
    }

    @Override
    public void removeItem(String toRemove) {
        throw new UnsupportedOperationException("Bukkit API has it's limitations");
    }

    @Override
    public List<Item> getItems() {
        throw new UnsupportedOperationException("Bukkit API has it's limitations");
    }

    @Override
    public String getUniqueId() {
        return objective.getName();
    }

    @Override
    public String getDisplayName() {
        return objective.getDisplayName();
    }

    @Override
    public void setDisplayName(String newName) {
        objective.setDisplayName(newName);
    }

    @Override
    public boolean isShown() {
        return objective.getDisplaySlot() == DisplaySlot.SIDEBAR;
    }

    @Override
    public void hide() {
        objective.setDisplaySlot(null);
    }

    @Override
    public void show() {
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    @Override
    public boolean exists() {
        try {
            objective.getName();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @Override
    public void remove() {
        objective.unregister();
    }
}
