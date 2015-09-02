package com.github.games647.scoreboardstats.scoreboard.protocol;

import com.github.games647.scoreboardstats.scoreboard.Item;
import com.github.games647.scoreboardstats.scoreboard.Group;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a sidebar objective
 *
 * @see PacketItem
 */
public class PacketObjective implements Group {

    private static final int MAX_ITEM_SIZE = 15;

    //A scoreboard can't have more than 15 items
    protected final Map<String, PacketItem> items = Maps.newHashMapWithExpectedSize(MAX_ITEM_SIZE);

    private final PacketScoreboard scoreboard;

    //objectiveName must be unique
    private final String objectiveName;
    private String displayName;

    PacketObjective(PacketScoreboard scoreboard, String objectiveName, String displayName) {
        this(scoreboard, objectiveName, displayName, true);
    }

    PacketObjective(PacketScoreboard scoreboard, String objectiveName, String displayName, boolean send) {
        this.scoreboard = scoreboard;

        this.objectiveName = objectiveName;
        this.displayName = displayName;

        if (send) {
            PacketFactory.sendPacket(this, State.CREATE);
        }
    }

    @Override
    public String getUniqueId() {
        return objectiveName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName)
            throws NullPointerException, IllegalArgumentException, IllegalStateException {
        displayNameChanged(displayName, true);
    }

    /**
     * Set the display name
     *
     * @param displayName the new displayName
     * @param send should the displayName instantly be sent
     * @throws NullPointerException if displayName is null
     * @throws IllegalArgumentException if displayName is longer than 32 characters
     * @throws IllegalStateException if this objective was removed
     */
    public void displayNameChanged(String displayName, boolean send)
            throws NullPointerException, IllegalArgumentException, IllegalStateException {
        Preconditions.checkState(exists());

        Preconditions.checkNotNull(displayName);
        Preconditions.checkArgument(displayName.length() <= 32);

        if (!this.displayName.equals(displayName)) {
            this.displayName = displayName;
            if (send) {
                PacketFactory.sendPacket(this, State.UPDATE);
            }
        }
    }

    /**
     * Add a new item/score with a predefined score to this objective.
     *
     * @param name the new scoreboard item
     * @param score the value for this scoreboard item
     * @param send should be the item send instantly
     * @return the created item
     * @throws NullPointerException if name is null
     * @throws IllegalArgumentException if name is longer than 16 characters
     * @throws IllegalStateException if this objective was removed
     * @throws IllegalStateException if this objective already has 15 scoreboard items
     */
    public PacketItem registerItem(String name, int score, boolean send)
            throws NullPointerException, IllegalArgumentException, IllegalStateException {
        Preconditions.checkState(exists());

        Preconditions.checkNotNull(name);
        //Since 1.8 the name can be up to 40 characters long. UUID in the future?
        ////newer minecraft versions support longer names - TODO: version specific check
        Preconditions.checkArgument(name.length() <= 48);

        Preconditions.checkState(items.size() <= MAX_ITEM_SIZE);

        final PacketItem scoreItem = new PacketItem(this, name, score, send);
        items.put(name, scoreItem);

        return scoreItem;
    }

    @Override
    public PacketItem getItem(String name) {
        return items.get(name);
    }

    @Override
    public List<? extends Item> getItems() {
        final List<PacketItem> values = Lists.newArrayList(items.values());
        Collections.sort(values);
        return ImmutableList.copyOf(values);
    }

    /**
     * Get the scoreboard instance.
     *
     * @return the scoreboard
     */
    public PacketScoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(objectiveName);
    }

    @Override
    public boolean equals(Object obj) {
        //ignores also null
        if (obj instanceof PacketObjective) {
            final PacketObjective other = (PacketObjective) obj;
            return Objects.equal(objectiveName, other.objectiveName);
        }

        return false;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public void addItems(Map<String, Integer> newItems) {
        for (Map.Entry<String, Integer> entrySet : newItems.entrySet()) {
            addItem(entrySet.getKey(), entrySet.getValue());
        }
    }

    @Override
    public PacketItem addItem(String... text) {
        PacketItem item = null;
        for (String name : text) {
            item = addItem(name, 0);
        }

        return item;
    }

    @Override
    public PacketItem addItem(String text, int score) {
        return registerItem(text, score, true);
    }

    @Override
    public void clear() {
        Preconditions.checkState(exists(), "the client doesn't know this objective");

        for (PacketItem item : items.values()) {
            item.remove();
        }

        items.clear();
    }

    @Override
    public void removeItem(String toRemove) {
        Preconditions.checkState(exists(), "the client doesn't know this objective");

        Preconditions.checkNotNull(toRemove, "name cannot be null");
        //newer minecraft versions support longer names - TODO: version specific check
//        Preconditions.checkArgument(name.length() <= 16, "a scoreboard item cannot be longer than 16 characters");

        final PacketItem item = items.remove(toRemove);
        if (item != null) {
            item.remove();
        }
    }

    @Override
    public boolean isShown() {
        //Prevents NPE with this ordering
        return this.equals(scoreboard.getCurrentSidebar());
    }

    @Override
    public void hide() {
        scoreboard.sidebarObjectiveCleared();
        PacketFactory.sendEmptySidebar(scoreboard.getOwner());
    }

    @Override
    public void show() {
        scoreboard.sidebarObjectiveChanged(this.getUniqueId());
        PacketFactory.sendDisplayPacket(this);
    }

    @Override
    public boolean exists() {
        return scoreboard.getObjective(objectiveName) == this;
    }

    @Override
    public void remove() {
        if (scoreboard.getObjective(objectiveName) == this) {
            scoreboard.objectiveRemoved(objectiveName);
            PacketFactory.sendPacket(this, State.REMOVE);
        }
    }
}
