package com.github.games647.scoreboardstats.scoreboard;

import java.util.List;
import java.util.Map;

public interface Group {

    /**
     * Add the items/scores containing in this map to the scoreboard
     *
     * @param newItems
     * @throws NullPointerException if newItems is null
     * @throws IllegalArgumentException if string is longer than 32 characters
     * @throws IllegalStateException if this objective was removed
     * @throws IllegalStateException if this objective already has 15 scoreboard items
     */
    void addItems(Map<String, Integer> newItems);

    /**
     * Add a or more new items/scores to this objective.
     *
     * @param text a list of new scoreboard items
     * @return the created item
     * @throws NullPointerException if name is null
     * @throws IllegalArgumentException if name is longer than 32 characters
     * @throws IllegalStateException if this objective was removed
     * @throws IllegalStateException if this objective already has 15 scoreboard items
     */
    Item addItem(String... text);

    /**
     * Add a new item/score with a predefined score to this objective.
     *
     * @param text the new scoreboard item name
     * @param score the value for this scoreboard item
     * @return the created item
     * @throws NullPointerException if name is null
     * @throws IllegalArgumentException if name is longer than 32 characters
     * @throws IllegalStateException if this objective was removed
     * @throws IllegalStateException if this objective already has 15 scoreboard items
     */
    Item addItem(String text, int score);

    /**
     * Gets the item/score with that name.
     *
     * @param name the name of the item
     * @return the item or null if no item item with that name exists
     */
    Item getItem(String name);

    /**
     * Remove all item/scores in this objective.
     *
     * @throws IllegalStateException if the objective was removed
     */
    void clear();

    /**
     * Removes an item/score from this objective.
     *
     * @param toRemove name of the score
     * @throws NullPointerException if name is null
     * @throws IllegalArgumentException if name is longer than 32 characters
     * @throws IllegalStateException if this objective was removed
     */
    void removeItem(String toRemove);

    /**
     * Gets a collection of all registered item/scores in this objective as an
     * immutable sorted list.
     *
     * @return all items for this objective
     */
    List<? extends Item> getItems();

    /**
     * Get the unique name (id) for this objective.
     *
     * @return the unique name for this objective
     */
    String getUniqueId();

    /**
     * Get the displayed name.
     *
     * @return the displayed name
     */
    String getDisplayName();

    /**
     * Set the display name
     *
     * @param newName the new displayName
     * @throws NullPointerException if displayName is null
     * @throws IllegalArgumentException if displayName is longer than 32 characters
     * @throws IllegalStateException if this objective was removed
     */
    void setDisplayName(String newName);

    /**
     * Check if this objective is shown on the sidebar
     *
     * @return whether this objective is shown on the sidebar slot
     */
    boolean isShown();

    /**
     * Hide the objective from the current display slot
     */
    void hide();

    /**
     * Show this objective on the sidebar
     */
    void show();

    /**
     * Check if this objective exists and is registered for the client
     *
     * @return whether this objective is registered
     */
    boolean exists();

    /**
     * Unregister this objective.
     */
    void remove();
}
