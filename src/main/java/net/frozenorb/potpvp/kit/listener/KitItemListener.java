package net.frozenorb.potpvp.kit.listener;

import net.frozenorb.potpvp.kit.KitItems;
import net.frozenorb.potpvp.kit.menu.kits.KitsMenu;
import net.frozenorb.potpvp.kittype.menu.SelectKitTypeMenu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public final class KitItemListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        if (event.getItem().isSimilar(KitItems.OPEN_EDITOR_ITEM)) {
            event.setCancelled(true);

            new SelectKitTypeMenu(kitType -> {
                new KitsMenu(kitType).openMenu(event.getPlayer());
            }).openMenu(event.getPlayer());
        }
    }

}