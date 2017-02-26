package net.frozenorb.potpvp.event;

import net.frozenorb.qlib.command.ParameterType;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EventTypeParameterType implements ParameterType<EventType> {

    @Override
    public EventType transform(CommandSender sender, String source) {
        for (EventType eventType : EventType.values()) {
            if (eventType.name().equalsIgnoreCase(source)) {
                return eventType;
            }
        }

        sender.sendMessage(ChatColor.RED + "No event type with the name " + source + " found.");
        return null;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        for (EventType eventType : EventType.values()) {
            if (StringUtils.startsWithIgnoreCase(eventType.name(), source)) {
                completions.add(eventType.name());
            }
        }

        return completions;
    }

}