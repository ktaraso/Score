package com.github.games647.scoreboardstats.scoreboard.bukkit;

import com.github.games647.scoreboardstats.scoreboard.Item;
import com.github.games647.scoreboardstats.scoreboard.Group;

import java.util.List;
import java.util.Map;

import org.bukkit.scoreboard.Objective;

public class BukkitObjective implements Group {

    private final Objective objective;

    public BukkitObjective(Objective objective) {
        this.objective = objective;
    }

    @Override
    public void addItems(Map<String, Integer> newItems) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public Item addItem(String... text) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public Item addItem(String text, int score) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public Item getItem(String name) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public void removeItem(String toRemove) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public List<Item> getItems() {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public String getUniqueId() {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public String getDisplayName() {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public void setDisplayName(String newName) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public boolean isShown() {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public void hide() {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public void show() {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public boolean exists() {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
