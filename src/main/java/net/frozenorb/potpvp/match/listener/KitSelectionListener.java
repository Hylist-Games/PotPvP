package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kit.Kit;
import net.frozenorb.potpvp.kit.KitHandler;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class KitSelectionListener implements Listener {

    /**
     * Don't let players drop their kit selection books via the Q key
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getPlayer());

        if (match == null) {
            return;
        }

        KitHandler kitHandler = PotPvPSI.getInstance().getKitHandler();
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        UUID playerUuid = event.getPlayer().getUniqueId();
        KitType kitType = match.getKitType();

        for (Kit kit : kitHandler.getKits(playerUuid, kitType)) {
            if (kit.isSelectionItem(droppedItem)) {
                event.setCancelled(true);
                return;
            }
        }

        Kit defaultKit = Kit.ofDefaultKit(kitType);

        if (defaultKit.isSelectionItem(droppedItem)) {
            event.setCancelled(true);
        }
    }

    /**
     * Don't let players drop their kit selection items via death
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getEntity());

        if (match == null) {
            return;
        }

        KitHandler kitHandler = PotPvPSI.getInstance().getKitHandler();
        UUID playerUuid = event.getEntity().getUniqueId();
        KitType kitType = match.getKitType();

        for (Kit kit : kitHandler.getKits(playerUuid, kitType)) {
            event.getDrops().remove(kit.createSelectionItem());
        }

        event.getDrops().remove(Kit.ofDefaultKit(kitType).createSelectionItem());
    }

    /**
     * Give players their kits upon right click
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getPlayer());

        if (match == null) {
            return;
        }

        KitHandler kitHandler = PotPvPSI.getInstance().getKitHandler();
        ItemStack clickedItem = event.getItem();
        KitType kitType = match.getKitType();
        Player player = event.getPlayer();

        for (Kit kit : kitHandler.getKits(player.getUniqueId(), kitType)) {
            if (kit.isSelectionItem(clickedItem)) {
                kit.apply(player);
                player.sendMessage(ChatColor.YELLOW + "You equipped your \"" + kit.getName() + "\" " + kitType.getName() + " kit.");
                return;
            }
        }

        Kit defaultKit = Kit.ofDefaultKit(kitType);

        if (defaultKit.isSelectionItem(clickedItem)) {
            defaultKit.apply(player);
            player.sendMessage(ChatColor.YELLOW + "You equipped the default kit for " + kitType.getName() + ".");
        }
    }

}