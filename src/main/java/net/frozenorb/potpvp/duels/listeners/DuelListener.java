package net.frozenorb.potpvp.duels.listeners;

import net.frozenorb.potpvp.duels.DuelHandler;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.match.event.MatchCountdownStartEvent;
import net.frozenorb.potpvp.match.event.MatchSpectatorJoinEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public final class DuelListener implements Listener {

    @EventHandler
    public void onMatchSpectatorJoin(MatchSpectatorJoinEvent event) {
        DuelHandler.instance().purgeInvitesTo(event.getSpectator());
    }

    @EventHandler
    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
        DuelHandler duelHandler = DuelHandler.instance();

        for (MatchTeam team : event.getMatch().getTeams()) {
            for (UUID member : team.getAllMembers()) {
                Player memberPlayer = Bukkit.getPlayer(member);
                duelHandler.purgeInvitesTo(memberPlayer);
            }
        }
    }

}