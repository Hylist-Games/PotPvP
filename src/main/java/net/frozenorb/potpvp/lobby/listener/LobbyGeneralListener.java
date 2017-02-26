package net.frozenorb.potpvp.lobby.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.qlib.menu.Menu;

import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.UUID;

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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        GameMode gameMode = event.getPlayer().getGameMode();

        if (lobbyHandler.isInLobby(event.getPlayer()) && gameMode != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

}