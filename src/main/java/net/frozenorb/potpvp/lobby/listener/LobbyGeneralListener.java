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

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        LobbyHandler lobbyHandler = PotPvPSI.getInstance().getLobbyHandler();
        event.setSpawnLocation(lobbyHandler.getLobbyLocation());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PotPvPSI.getInstance().getLobbyHandler().returnToLobby(event.getPlayer());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        UUID entityUuid = event.getEntity().getUniqueId();

        if (!matchHandler.isPlayingOrSpectatingMatch(entityUuid)) {
            // return players who fell off the map to spawn.
            if (event.getEntityType() == EntityType.PLAYER && event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                Player player = (Player) event.getEntity();
                PotPvPSI.getInstance().getLobbyHandler().returnToLobby(player);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        UUID entityUuid = event.getEntity().getUniqueId();

        if (!matchHandler.isPlayingOrSpectatingMatch(entityUuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (!matchHandler.isPlayingOrSpectatingMatch(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getPlayer();

        if (matchHandler.isPlayingOrSpectatingMatch(player)) {
            return;
        }

        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());

        // just remove the item for players in these menus, so they can 'drop' items to remove them
        if (openMenu != null && openMenu.isNoncancellingInventory()) {
            event.getItemDrop().remove();
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getPlayer();

        if (matchHandler.isPlayingOrSpectatingMatch(player) && player.getGameMode() == GameMode.SURVIVAL) {
            return;
        }

        event.setCancelled(true);
    }

}