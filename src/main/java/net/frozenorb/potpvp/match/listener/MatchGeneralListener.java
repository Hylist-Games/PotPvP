package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.Arena;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchState;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.qlib.util.PlayerUtils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public final class MatchGeneralListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getEntity();
        Match match = matchHandler.getMatchPlaying(player);

        if (match == null) {
            return;
        }

        MatchTeam team = match.getTeam(player.getUniqueId());

        // creates 'proper' player death animation (of the player falling over)
        // which we don't get due to our immediate respawn
        PlayerUtils.animateDeath(player);

        team.markDead(player.getUniqueId());
        match.checkEnded();
        match.addSpectator(player, null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getPlayer();
        Match match = matchHandler.getMatchPlaying(player);

        if (match == null) {
            return;
        }

        MatchState state = match.getState();

        if (state == MatchState.COUNTDOWN || state == MatchState.IN_PROGRESS) {
            MatchTeam team = match.getTeam(player.getUniqueId());

            team.markDead(player.getUniqueId());
            match.checkEnded();
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        // PlayerTeleportEvent extends PlayerMoveEvent, so we can just
        // 'forward' this event down to our move handler.
        onPlayerMove(event);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (
            from.getBlockX() == to.getBlockX() &&
            from.getBlockY() == to.getBlockY() &&
            from.getBlockZ() == to.getBlockZ()
        ) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(player);

        if (match == null) {
            return;
        }

        Arena arena = match.getArena();

        if (!arena.getBounds().contains(to)) {
            // spectators get a nice message, players
            // just get cancelled
            if (match.isSpectator(player.getUniqueId())) {
                player.teleport(arena.getSpectatorSpawn());
                player.sendMessage(ChatColor.RED + "You aren't allowed to leave the arena.");
            } else {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Prevent friendly fire
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player victim = (Player) event.getEntity();
        Player damager = PlayerUtils.getDamageSource(event.getDamager());

        if (damager == null) {
            return;
        }

        Match match = matchHandler.getMatchPlaying(damager);

        if (match == null) {
            return;
        }

        MatchTeam damagerTeam = match.getTeam(damager.getUniqueId());

        boolean pearlDamage = event.getCause() == EntityDamageEvent.DamageCause.FALL;
        boolean sameTeam = damagerTeam != null && damagerTeam.isAlive(victim.getUniqueId());

        if (!pearlDamage && sameTeam) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getPlayer();

        if (!matchHandler.isPlayingMatch(player)) {
            return;
        }

        ItemStack itemStack = event.getItemDrop().getItemStack();
        Material itemType = itemStack.getType();
        String itemTypeName = itemType.name().toLowerCase();
        int heldSlot = player.getInventory().getHeldItemSlot();

        // don't let players drop swords, axes, and bows in the first slot
        if (heldSlot != 0 && (itemTypeName.contains("sword") || itemTypeName.contains("axe") || itemTypeName.contains("bow"))) {
            player.sendMessage(ChatColor.RED + "You can't drop that while you're holding it in slot 1.");
            event.setCancelled(true);
        }

        // glass bottles are removed from inventories but
        // don't spawn items on the ground
        if (itemType == Material.GLASS_BOTTLE) {
            event.getItemDrop().remove();
        }
    }

}