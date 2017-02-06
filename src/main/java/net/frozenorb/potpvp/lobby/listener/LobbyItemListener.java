package net.frozenorb.potpvp.lobby.listener;

import net.frozenorb.potpvp.lobby.LobbyItems;

import net.frozenorb.potpvp.util.ItemListener;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public final class LobbyItemListener extends ItemListener {

    public LobbyItemListener() {
        addHandler(LobbyItems.EVENTS_ITEM, p -> p.sendMessage(ChatColor.RED + "Events are not yet completed! They will be done soon!"));
    }

}