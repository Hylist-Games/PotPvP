package net.frozenorb.potpvp.postmatchinv.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.event.MatchTerminateEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class PostMatchInvGeneralListener implements Listener {

    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        PotPvPSI.getInstance().getPostMatchInvHandler().registerInventories(event.getMatch());
    }

}