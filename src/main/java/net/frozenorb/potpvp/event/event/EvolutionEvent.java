package net.frozenorb.potpvp.event.event;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.event.EventType;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class EvolutionEvent extends MatchBackedEvent {

    public EvolutionEvent() {
        super(EventType.EVOLUTION);
    }

    @Override
    public void startEvent(Set<UUID> queued) {

    }

    @Override
    public List<String> getLiveStatus() {
        return ImmutableList.of(
            ChatColor.WHITE + "Top 3:",
            ChatColor.WHITE + "  5 " + ChatColor.YELLOW + "Stimpay",
            ChatColor.WHITE + "  5 " + ChatColor.YELLOW + "itsjhalt",
            ChatColor.WHITE + "  4 " + ChatColor.YELLOW + "macguy8"
        );
    }

}