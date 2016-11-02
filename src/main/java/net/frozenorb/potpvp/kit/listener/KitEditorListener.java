package net.frozenorb.potpvp.kit.listener;

import net.frozenorb.potpvp.kit.menu.editkit.EditKitMenu;
import net.frozenorb.qlib.menu.Menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.PlayerInventory;

public final class KitEditorListener implements Listener {

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (!(event.getSource() instanceof PlayerInventory)) {
            return;
        }

        PlayerInventory playerInv = (PlayerInventory) event.getSource();
        Player player = (Player) playerInv.getHolder();

        Menu openMenu = Menu.currentlyOpenedMenus.get(player);

        if (openMenu instanceof EditKitMenu) {
            event.setCancelled(true);
        }
    }

}