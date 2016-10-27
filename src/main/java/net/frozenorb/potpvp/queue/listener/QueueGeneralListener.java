package net.frozenorb.potpvp.queue.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.match.event.MatchCountdownStartEvent;
import net.frozenorb.potpvp.match.event.MatchStartEvent;
import net.frozenorb.potpvp.queue.QueueHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class QueueGeneralListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        queueHandler.leaveQueue(event.getPlayer());
    }

    @EventHandler
    public void onMatchStart(MatchCountdownStartEvent event) {
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();

        for (MatchTeam team : event.getMatch().getTeams()) {
            for (UUID member : team.getAllMembers()) {
                Player memberBukkit = Bukkit.getPlayer(member);
                queueHandler.leaveQueue(memberBukkit);
            }
        }
    }

}