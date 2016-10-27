package net.frozenorb.potpvp.queue.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.match.event.MatchCountdownStartEvent;
import net.frozenorb.potpvp.match.event.MatchSpectatorJoinEvent;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.party.event.PartyDisbandEvent;
import net.frozenorb.potpvp.queue.QueueHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class QueueGeneralListener implements Listener {

    @EventHandler
    public void onPartyDisband(PartyDisbandEvent event) {
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        queueHandler.leaveQueue(event.getParty(), true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        queueHandler.leaveQueue(event.getPlayer(), true);
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