package net.frozenorb.potpvp.event.event;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.event.Event;
import net.frozenorb.potpvp.event.EventType;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.qlib.qLib;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class KothEvent extends MatchBackedEvent {

    public KothEvent() {
        super(EventType.KOTH);
    }

    @Override
    public void startEvent(Set<UUID> queued) {
        /*MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        match = matchHandler.startMatch(
            ImmutableList.of(new MatchTeam(queued)),
            KitType.byId("ARCHER"),
            false,
            false
        );*/
    }

    @Override
    public List<String> getLiveStatus() {
        return ImmutableList.of(
            ChatColor.YELLOW + "Eddythepro" + ChatColor.WHITE + " capturing",
            ChatColor.YELLOW + "5:15" + ChatColor.WHITE + " remaining"
        );
    }

}