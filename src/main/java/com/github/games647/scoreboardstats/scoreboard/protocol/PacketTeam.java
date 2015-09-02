package com.github.games647.scoreboardstats.scoreboard.protocol;

public class PacketTeam {

    private final PacketScoreboard scoreboard;

    private final String uniqueId;

    private String displayName;
    private String prefix;
    private String suffix;

    public PacketTeam(PacketScoreboard scoreboard, String uniqueId, String displayName, String prefix, String suffix) {
        this.scoreboard = scoreboard;

        this.uniqueId = uniqueId;
        this.displayName = displayName;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public PacketScoreboard getScoreboard() {
        return scoreboard;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
