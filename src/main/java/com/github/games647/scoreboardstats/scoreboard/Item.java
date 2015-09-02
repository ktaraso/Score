package com.github.games647.scoreboardstats.scoreboard;

public interface Item extends Comparable<Item> {

    /**
     * Get the current score.
     *
     * @return the current score
     */
    int getScore();

    /**
     * Set the score value.
     *
     * @param newScore the new value
     * @throws IllegalStateException if the item was removed
     * @throws IllegalStateException if the objective was removed
     */
    void setScore(int newScore);

    /**
     * Get the id for this item. 
     *
     * @return
     */
    String getUniqueId();

    /**
     * Get the display name for this item.
     *
     * @return the unique item name
     */
    String getDisplayName();

    /**
     * Set the display name for this item
     *
     * @param newName new display name
     * @throws IllegalArgumentException if newName is longer than 32 characters
     */
    void setDisplayName(String newName);

    /**
     * Check whether this item exists
     *
     * @return true if the client sees this score
     */
    boolean exists();

    /**
     * Unregister this item.
     *
     * Remove per objective is only implemented in 1.8
     */
    void remove();

    /**
     * Get the associated parent.
     *
     * @return the objective that hold this item
     */
    Group getParent();
}
