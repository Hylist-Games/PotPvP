package net.frozenorb.potpvp.queue.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.match.event.MatchCountdownStartEvent;
import net.frozenorb.potpvp.match.event.MatchSpectatorJoinEvent;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.party.event.PartyDisbandEvent;
import net.frozenorb.potpvp.party.event.PartyMemberJoinEvent;
import net.frozenorb.potpvp.party.event.PartyMemberKickEvent;
import net.frozenorb.potpvp.party.event.PartyMemberLeaveEvent;
import net.frozenorb.potpvp.queue.QueueHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class QueueGeneralListener implements Listener {

    @EventHandler
    public void onPartyDisband(PartyDisbandEvent event) {
        PotPvPSI.getInstance().getQueueHandler().leaveQueue(event.getParty(), true);
    }

    @EventHandler
    public void onPartyMemberJoin(PartyMemberJoinEvent event) {
        leaveQueue(event.getParty(), event.getMember(), "joined");
    }

    @EventHandler
    public void onPartyMemberKick(PartyMemberKickEvent event) {
        leaveQueue(event.getParty(), event.getMember(), "was kicked");
    }

    @EventHandler
    public void onPartyMemberLeave(PartyMemberLeaveEvent event) {
        leaveQueue(event.getParty(), event.getMember(), "left");
    }

    private void leaveQueue(Party party, Player member, String action) {
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();

        if (queueHandler.leaveQueue(party, true)) {
            String memberName = member.getName();
            party.message(ChatColor.YELLOW + "Your party has been removed from the queue because " + memberName + " " + action + ".");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PotPvPSI.getInstance().getQueueHandler().leaveQueue(event.getPlayer(), true);
    }

    @EventHandler
    public void onMatchSpectatorJoin(MatchSpectatorJoinEvent event) {
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        queueHandler.leaveQueue(event.getSpectator(), true);
    }

    @EventHandler
    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();

        for (MatchTeam team : event.getMatch().getTeams()) {
            for (UUID member : team.getAllMembers()) {
                Player memberBukkit = Bukkit.getPlayer(member);
                Party memberParty = partyHandler.getParty(memberBukkit);

                queueHandler.leaveQueue(memberBukkit, true);

                if (memberParty != null && memberParty.isLeader(member)) {
                    queueHandler.leaveQueue(memberParty, true);
                }
            }
        }
    }

}