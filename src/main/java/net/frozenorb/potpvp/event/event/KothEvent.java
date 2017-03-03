package net.frozenorb.potpvp.event.event;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.event.EventType;

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