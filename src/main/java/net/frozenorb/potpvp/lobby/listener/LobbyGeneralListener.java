package net.frozenorb.potpvp.lobby.listener;

import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public final class LobbyGeneralListener implements Listener {

    private final LobbyHandler lobbyHandler;

    public LobbyGeneralListener(LobbyHandler lobbyHandler) {
        this.lobbyHandler = lobbyHandler;
    }

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        event.setSpawnLocation(lobbyHandler.getLobbyLocation());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        lobbyHandler.returnToLobby(event.getPlayer());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (lobbyHandler.isInLobby(player)) {
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                lobbyHandler.returnToLobby(player);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (lobbyHandler.isInLobby((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (lobbyHandler.isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!lobbyHandler.isInLobby(event.getPlayer())) {
            return;
        }

        Menu openMenu = Menu.currentlyOpenedMenus.get(event.getPlayer().getName());

        // just remove the item for players in these menus, so they can 'drop' items to remove them
        if (openMenu != null && openMenu.isNoncancellingInventory()) {
            event.getItemDrop().remove();
        } else {
            event.setCancelled(true);
        }
    }

    // cancel inventory interaction in the lobby except for menus
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!lobbyHandler.isInLobby((Player) event.getWhoClicked()) || Menu.currentlyOpenedMenus.containsKey(event.getWhoClicked().getName())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!lobbyHandler.isInLobby((Player) event.getWhoClicked()) || Menu.currentlyOpenedMenus.containsKey(event.getWhoClicked().getName())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (lobbyHandler.isInLobby(event.getEntity())) {
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        InventoryHolder inventoryHolder = event.getSource().getHolder();

        if (inventoryHolder instanceof Player) {
            Player player = (Player) inventoryHolder;

            if (!lobbyHandler.isInLobby(player) || Menu.currentlyOpenedMenus.containsKey(player.getName())) {
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        GameMode gameMode = event.getPlayer().getGameMode();

        if (lobbyHandler.isInLobby(event.getPlayer()) && gameMode != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }

        if (lobbyHandler.isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

}