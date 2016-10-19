package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchUtils;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.event.SettingUpdateEvent;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.projectiles.ProjectileSource;

import java.util.UUID;

public final class SpectatorPreventionListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchSpectating(event.getPlayer());

        if (match != null) {
            match.removeSpectator(event.getPlayer());
        }
    }

    /**
     * Prevent spectator items from dropping in the rare case spectators die
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchSpectating(event.getEntity());

        if (match != null) {
            event.setKeepInventory(true);
        }
    }

    /**
     * Prevent damage caused by spectators
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
            Player damager = (Player) event.getDamager();

            if (matchHandler.isSpectatingMatch(damager)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Prevent item drops by spectators
     */
    @EventHandler
    public void onPlayerDropitem(PlayerDropItemEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (matchHandler.isSpectatingMatch(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevent item pickups by spectators
     */
    @EventHandler
    public void onPlayerPickupitem(PlayerPickupItemEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (matchHandler.isSpectatingMatch(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (matchHandler.isSpectatingMatch(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (matchHandler.isSpectatingMatch(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        InventoryHolder inventoryHolder = event.getSource().getHolder();

        if (inventoryHolder instanceof Player) {
            MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
            UUID playerUuid = ((Player) inventoryHolder).getUniqueId();

            if (matchHandler.isSpectatingMatch(playerUuid)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSettingUpdate(SettingUpdateEvent event) {
        if (event.getSetting() == Setting.VIEW_OTHER_SPECTATORS) {
            MatchUtils.updateVisibility(event.getPlayer());
        }
    }

    // two things are happening in the same event here
    // we're both disallow spectators throwing potions and
    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        Projectile entity = event.getEntity();
        ProjectileSource shooter = entity.getShooter();

        if (!(shooter instanceof Player)) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = (Player) shooter;
        Match match = matchHandler.getMatchSpectating(player);

        if (match != null) {
            event.setCancelled(true);
        }

        // don't give spectators effects from potions splashed near them
        for (LivingEntity affectedEntity : event.getAffectedEntities()) {
            if (affectedEntity instanceof Player) {
                Match affectedEntityMatch = matchHandler.getMatchSpectating(affectedEntity.getUniqueId());

                if (affectedEntityMatch != null) {
                    event.setIntensity(affectedEntity, 0F);
                }
            }
        }
    }

}