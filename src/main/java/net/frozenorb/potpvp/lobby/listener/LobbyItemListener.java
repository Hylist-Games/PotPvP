package net.frozenorb.potpvp.lobby.listener;

import net.frozenorb.potpvp.lobby.LobbyItems;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public final class LobbyItemListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        if (event.getItem().isSimilar(LobbyItems.EVENTS_ITEM)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Events are not yet completed! They will be done soon!");
        }
    }

}