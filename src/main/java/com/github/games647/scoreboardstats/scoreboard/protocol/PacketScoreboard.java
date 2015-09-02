package com.github.games647.scoreboardstats.scoreboard.protocol;

import com.github.games647.scoreboardstats.scoreboard.Group;
import com.github.games647.scoreboardstats.scoreboard.Scoreboard;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

import org.bukkit.entity.Player;

/**
 * Represents the scoreboard overview in a server-side perspective.
 */
public class PacketScoreboard implements Scoreboard {

    private final Player player;

    private final Map<String, PacketObjective> objectivesByName = Maps.newHashMap();
    private final Map<String, PacketTeam> teamsByName = Maps.newHashMap();
    private final Map<String, PacketTeam> teamsByMember = Maps.newHashMap();

    private PacketObjective sidebarObjective;

    //start at 200 to prevent collisions with the color codes
    private int lastId = 200;

    /**
     * Creates a new scoreboard for specific player.
     *
     * @param player the player for the scoreboard
     */
    public PacketScoreboard(Player player) {
        this.player = player;
    }

    @Override
    public ImmutableSet<PacketObjective> getGroups() {
        return ImmutableSet.copyOf(objectivesByName.values());
    }

    /**
     * Gets the owner of this scoreboard
     *
     * @return the owner of the scoreboard
     */
    public Player getOwner() {
        return player;
    }

    @Override
    public PacketObjective getCurrentSidebar() {
        return sidebarObjective;
    }

    @Override
    public Group addObjective(String uniqueName) {
        PacketObjective packetObjective = new PacketObjective(this, uniqueName, uniqueName);
        objectivesByName.put(uniqueName, packetObjective);
        sidebarObjective = packetObjective;
//        if () {
//            Preconditions.checkState(sidebarObjective == null, "There is already an sidebar objective");
//        }
        return packetObjective;
    }

    @Override
    public Group addObjective(String uniqueName, String displayName) {
        PacketObjective packetObjective = new PacketObjective(this, uniqueName, displayName);
        objectivesByName.put(uniqueName, packetObjective);
        sidebarObjective = packetObjective;
        return packetObjective;
    }

    @Override
    public void removeItemGlobal(String name) {
        for (PacketObjective entry : objectivesByName.values()) {
            entry.removeItem(name);
        }
    }

    @Override
    public PacketObjective getObjective(String name) {
        return objectivesByName.get(name);
    }

    protected Group objectiveAdded(String uniqueName, String displayName) {
        PacketObjective packetObjective = new PacketObjective(this, uniqueName, displayName, false);
        objectivesByName.put(uniqueName, packetObjective);
        return packetObjective;
    }

    protected void sidebarObjectiveChanged(String objectiveName) {
        if (objectiveName.isEmpty()) {
            sidebarObjectiveCleared();
        } else {
            sidebarObjective = objectivesByName.get(objectiveName);
        }
    }

    protected void objectiveRemoved(String objectiveName) {
        objectivesByName.remove(objectiveName);
        if (sidebarObjective != null && sidebarObjective.getUniqueId().equals(objectiveName)) {
            sidebarObjectiveCleared();
        }
    }

    protected void sidebarObjectiveCleared() {
        sidebarObjective = null;
    }

    protected void scoreRemoved(String scoreName) {
        /*
         * Very weird that minecraft always ignore the name of the parent objective and
         * will remove the score from the complete scoreboard
         */
        for (PacketObjective entry : objectivesByName.values()) {
            entry.items.remove(scoreName);
        }
    }

    protected void scoreCreatedOrChanged(String scoreName, String parent, int score) {
        final PacketObjective objective = objectivesByName.get(parent);
        if (objective != null) {
            final PacketItem item = new PacketItem(objective, scoreName, score, false);
            //This automatically replace the old one
            objective.items.put(scoreName, item);
        }
    }

    protected void teamCreated(String name, String displayName, String prefix, String suffix
            , Collection<String> players) {
        PacketTeam team = teamsByName.get(name);
        if (team == null) {
            team = new PacketTeam(this, name, displayName, prefix, suffix);
            teamsByName.put(name, team);
            for (String member : players) {
                teamsByMember.put(member, team);
            }
        }
    }

    protected void teamInfoChanged(String name, String displayName, String prefix, String suffix) {
        PacketTeam team = teamsByName.get(name);
        if (team != null) {
            team.setDisplayName(displayName);
            team.setPrefix(prefix);
            team.setSuffix(suffix);
        }
    }

    protected void teamRemoved(String name) {
        teamsByName.remove(name);
    }

    protected void teamPlayerAdded(String name, Collection<String> addedPlayers) {
        PacketTeam team = teamsByName.get(name);
        if (team != null) {
            for (String newMember : addedPlayers) {
                teamsByMember.put(newMember, team);
            }
        }
    }

    protected void teamPlayerRemoved(String name, Collection<String> removedPlayers) {
        PacketTeam team = teamsByName.get(name);
        if (team != null) {
            for (String removedMember : removedPlayers) {
                teamsByMember.remove(removedMember);
            }
        }
    }

    public PacketTeam getTeamForPlayer(String player) {
        return teamsByMember.get(player);
    }

    protected String generateId(String displayName) {
        String prefix = "";
        String suffix = "";
        if (displayName.length() > 16) {
            prefix = displayName.substring(0, 16);
            suffix = displayName.substring(16);
        } else {
            //prioritize to use suffix so we don't need to check the previous colors
            suffix = displayName.substring(0, displayName.length());
        }

        String uniqueId = "§" + (char) lastId;

        PacketTeam newTeam = new PacketTeam(this, "SBS-" + uniqueId, "SBS-" + uniqueId, prefix, suffix);
        teamsByName.put("SBS-" + uniqueId, newTeam);
        teamsByMember.put(uniqueId, newTeam);
        PacketFactory.sendTeamPacket(newTeam, uniqueId, State.CREATE);

//        String lastColors = ChatColor.getLastColors(prefix);
//        return uniqueId + ChatColor.RESET + lastColors;
        lastId++;
        return uniqueId;
    }
}
