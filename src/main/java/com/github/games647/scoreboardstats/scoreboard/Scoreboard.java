package com.github.games647.scoreboardstats.scoreboard;

import com.google.common.collect.ImmutableSet;

public interface Scoreboard {

    /**
     * Get the current sidebar objective
     *
     * @return the sidebar objective or null if there is no
     */
    Group getCurrentSidebar();

    /**
     * Get the objective by the unique name
     *
     * @param uniqueName unique name of the objective
     * @return the objetive or null if not found
     * @throws IllegalArgumentException if uniqueName is longer than 16 characters
     */
    Group getObjective(String uniqueName);

    /**
     * Add a new objective
     *
     * @param uniqueName a unique to recognize the objective
     * @return the created objective
     * @throws IllegalArgumentException if uniqueName is longer than 16 characters
     */
    Group addObjective(String uniqueName);

    /**
     * Add a new objective with display name different than the unique name
     *
     * @param uniqueName
     * @param displayName
     * @return the created objective
     * @throws IllegalArgumentException if uniqueName is longer than 16 characters
     * @throws IllegalArgumentException if displayName is longer than 32 characters
     */
    Group addObjective(String uniqueName, String displayName);

    /**
     * Get all existing objectives
     *
     * @return all existing objectives
     */
    ImmutableSet<? extends Group> getGroups();

    /**
     * Remove all scoreboard items with the same name from
     * all objectives.
     *
     * @param name scoreboard item name
     * @throws IllegalArgumentException if name is longer than 40 characters
     */
    void removeItemGlobal(String name);
}
