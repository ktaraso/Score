package com.github.games647.scoreboardstats.scoreboard.protocol;

import com.github.games647.scoreboardstats.scoreboard.Item;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a scoreboard item with ProtocolLib.
 *
 * @see PacketObjective
 */
public class PacketItem implements Item {

    private final PacketObjective parent;

    private final String uniqueId;

    private String scoreName;
    private int score;

    PacketItem(PacketObjective parent, String scoreName, int score) {
        this(parent, scoreName, score, true);
    }

    PacketItem(PacketObjective parent, String scoreName, int score, boolean send) {
        this.parent = parent;

        this.scoreName = scoreName;
        this.score = score;

        if (send) {
            this.uniqueId = parent.getScoreboard().generateId(scoreName);
            update();
        } else {
            this.uniqueId = scoreName;
        }
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void setScore(int score) throws IllegalStateException {
        Preconditions.checkState(exists(), "the parent objective or this item isn't active");

        if (this.score != score) {
            this.score = score;
            update();
        }
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public String getDisplayName() {
        return scoreName;
    }

    @Override
    public void setDisplayName(String newName) {
        if (!scoreName.equals(newName)) {
            if (uniqueId.equals(scoreName)) {
                //not a generated id
                throw new IllegalStateException("This scoreboard item has no generated id");
            }

            this.scoreName = newName;
            PacketTeam team = parent.getScoreboard().getTeamForPlayer(uniqueId);
            if (team != null) {
                String prefix = "";
                String suffix = "";
                if (newName.length() > 16) {
                    prefix = newName.substring(0, 16);
                    suffix = newName.substring(16);
                } else {
                    //prioritize to use suffix so we don't need to check the previous colors
                    suffix = newName.substring(0, newName.length());
                }

                team.setPrefix(prefix);
                team.setSuffix(suffix);

                PacketFactory.sendTeamPacket(team, null, State.UPDATE);
            }
        }
    }

    @Override
    public boolean exists() {
        return parent.items.containsValue(this) && parent.exists();
    }

    @Override
    public void remove() {
        if (exists()) {
            parent.getScoreboard().scoreRemoved(scoreName);
            PacketFactory.sendPacket(this, State.REMOVE);
            PacketTeam team = parent.getScoreboard().getTeamForPlayer(uniqueId);
            if (team != null) {
                PacketFactory.sendTeamPacket(team, null, State.REMOVE);
            }
        }
    }

    @Override
    public PacketObjective getParent() {
        return parent;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(scoreName);
    }

    @Override
    public boolean equals(Object obj) {
        //ignores also null
        if (obj instanceof PacketItem) {
            final PacketItem other = (PacketItem) obj;
            return Objects.equal(scoreName, other.scoreName);
        }

        return false;
    }

    @Override
    public int compareTo(Item other) {
        //Reverse order - first the highest element like the scoreboard ingame
        return Ints.compare(other.getScore(), score);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Sends a new packet
     *
     * @see PacketFactory
     */
    private void update() {
        PacketFactory.sendPacket(this, State.CREATE);
    }
}
