package com.github.games647.scoreboardstats.scoreboard.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;

/**
 * Listening all outgoing packets and check + handle for possibly client crash cases.
 * This Listener should only read and listen to relevant packets.
 *
 * Protocol specifications can be found here http://wiki.vg/Protocol
 *
 * @see PacketFactory
 * @see PacketAdapter
 */
public class PacketListener extends PacketAdapter {

    //Shorter access
    private static final PacketType DISPLAY_TYPE = PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE;
    private static final PacketType OBJECTIVE_TYPE = PacketType.Play.Server.SCOREBOARD_OBJECTIVE;
    private static final PacketType SCORE_TYPE = PacketType.Play.Server.SCOREBOARD_SCORE;
    private static final PacketType TEAM_TYPE = PacketType.Play.Server.SCOREBOARD_TEAM;

    private final PacketSbManager manager;

    /**
     * Creates a new packet listener
     *
     * @param plugin plugin for registration into ProtcolLib
     * @param manager packet manager instance
     */
    public PacketListener(Plugin plugin, PacketSbManager manager) {
        super(plugin, DISPLAY_TYPE, OBJECTIVE_TYPE, SCORE_TYPE, TEAM_TYPE);

        this.manager = manager;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final PacketType packetType = event.getPacketType();
        if (packetType.equals(SCORE_TYPE)) {
            handleScorePacket(event);
        } else if (packetType.equals(OBJECTIVE_TYPE)) {
            handleObjectivePacket(event);
        } else if (packetType.equals(DISPLAY_TYPE)) {
            handleDisplayPacket(event);
        } else if (packetType.equals(TEAM_TYPE)) {
            handleTeamPacket(event);
        }
    }

    private void handleScorePacket(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();

        final String scoreName = packet.getStrings().read(0);
        final String parent = packet.getStrings().read(1);
        final int score = packet.getIntegers().read(0);

        //state id
        final StructureModifier<Enum> enumModifier = packet.getSpecificModifier(Enum.class);
        final Enum<?> scoreboardActions = enumModifier.readSafely(0);

        State action;
        if (scoreboardActions == null) {
            //old system
            action = State.fromId(packet.getIntegers().read(1));
        } else {
            action = State.fromId(scoreboardActions.ordinal());
        }

        //Packet receiving validation
        if (action == State.CREATE && parent.length() > 16) {
            //Invalid packet
            return;
        }

        final PacketScoreboard scoreboard = manager.getOrCreateScoreboard(player);
        //scores actually only have two state id, because these
        if (action == State.CREATE) {
            scoreboard.scoreCreatedOrChanged(scoreName, parent, score);
        } else if (action == State.REMOVE) {
            scoreboard.scoreRemoved(scoreName);
        }
    }

    private void handleObjectivePacket(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();

        final String objectiveName = packet.getStrings().read(0);
        //Can be empty
        final String displayName = packet.getStrings().read(1);
        final State action = State.fromId(packet.getIntegers().read(0));

        //Packet receiving validation
        if (objectiveName.length() > 16 || displayName.length() > 32) {
            //Invalid packet
            return;
        }

        final PacketScoreboard scoreboard = manager.getOrCreateScoreboard(player);
        final PacketObjective objective = scoreboard.getObjective(objectiveName);
        if (action == State.CREATE) {
            scoreboard.objectiveAdded(objectiveName, displayName);
        } else if (objective != null) {
            //Could cause a NPE at the client if the objective wasn't found
            if (action == State.REMOVE) {
                scoreboard.objectiveRemoved(objectiveName);
            } else if (action == State.UPDATE) {
                objective.displayNameChanged(displayName, false);
            }
        }
    }

    private void handleDisplayPacket(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();

        //Can be empty; if so it would just clear the slot
        final String objectiveName = packet.getStrings().read(0);
        final DisplaySlot slot = SlotTransformer.fromId(packet.getIntegers().read(0));

        //Packet receiving validation
        if (slot == null || objectiveName.length() > 16) {
            return;
        }

        final PacketScoreboard scoreboard = manager.getOrCreateScoreboard(player);
        if (slot == DisplaySlot.SIDEBAR) {
            scoreboard.sidebarObjectiveChanged(objectiveName);
        } else {
            final PacketObjective sidebarObjective = scoreboard.getCurrentSidebar();
            if (sidebarObjective != null && sidebarObjective.getUniqueId().equals(objectiveName)) {
                scoreboard.sidebarObjectiveCleared();
            }
        }
    }

    private void handleTeamPacket(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();

        final PacketScoreboard scoreboard = manager.getOrCreateScoreboard(player);

        final String name = packet.getStrings().read(0);

        String displayName = packet.getStrings().read(1);
        String prefix = packet.getStrings().read(2);
        String suffix = packet.getStrings().read(3);

        Collection<String> players = packet.getSpecificModifier(Collection.class).read(0);

        final int state = packet.getIntegers().read(1);
        switch (state) {
            case 0:
                //created
                scoreboard.teamCreated(name, displayName, prefix, suffix, players);
                break;
            case 2:
                //team info updated
                scoreboard.teamInfoChanged(name, displayName, prefix, suffix);
                break;
            case 1:
                //removed
                scoreboard.teamRemoved(name);
                break;
            case 3:
                //new player
                scoreboard.teamPlayerAdded(name, players);
                break;
            case 4:
            default:
                //player removed
                scoreboard.teamPlayerRemoved(name, players);
                break;
        }
    }
}
