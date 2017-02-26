package net.frozenorb.potpvp.event.menu;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.event.Event;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.util.Callback;
import net.frozenorb.qlib.util.TimeUtils;
import net.frozenorb.qlib.util.UUIDUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

final class EventButton extends Button {

    private final Event event;

    EventButton(Event event) {
        this.event = event;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.AQUA.toString() + ChatColor.BOLD + event.getType().getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();

        String hostStr = event.getHost() == null ? "server" : UUIDUtils.name(event.getHost());
        description.add("Hosted by " + ChatColor.GOLD + hostStr);

        if (event.isActive()) {
            description.add("Started " + ChatColor.AQUA + TimeUtils.formatIntoMMSS((int) ChronoUnit.SECONDS.between(Instant.now(), event.getStarted())) + ChatColor.WHITE + " ago");
        } else {
            description.add("Starts in " + ChatColor.AQUA + TimeUtils.formatIntoMMSS(event.getCountdown()));
        }

        description.add("");

        for (String gameDescLine : event.getType().getDescription()) {
            description.add(ChatColor.GREEN + gameDescLine);
        }

        description.add("");

        if (event.isActive()) {
            description.addAll(event.getLiveStatus());
        } else {
            description.add(ChatColor.AQUA.toString() + event.getPlayers().size() + ChatColor.WHITE + " players (max " + event.getType().getMaxPlayers() + ")");
            description.add(ChatColor.GREEN + "» Click to queue «");
        }

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return event.getType().getIcon().getItemType();
    }

    @Override
    public byte getDamageValue(Player player) {
        return event.getType().getIcon().getData();
    }

    @Override
    public int getAmount(Player player) {
        return event.getPlayers().size();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (!event.isActive()) {
            player.sendMessage(ChatColor.GREEN + "Joining!!!");
        }
    }

}