package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.qlib.util.PlayerUtils;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public final class KitSelectionListener implements Listener {

    /**
     * Don't let players drop their kit selection books via the Q key
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Match match = PotPvPSlave.getInstance().getMatchHandler().getMatchPlaying(event.getPlayer().getUniqueId());

        if (match == null) {
            return;
        }

        KitHandler kitHandler = PotPvPSlave.getInstance().getKitHandler();
        KitType kitType = match.getKitType();
        ItemStack itemStack = event.getItemDrop().getItemStack();

        for (Kit kit : kitHandler.getKits(event.getPlayer().getUniqueId(), kitType)) {
            if (kit.isSelectionItem(itemStack)) {
                event.setCancelled(true);
                break;
            }
        }

        Kit defaultKit = kitType.createDefaultKit();

        if (defaultKit.isSelectionItem(itemStack)) {
            event.setCancelled(true);
        }
    }

    /**
     * Give new players their kit selection items when logging in to a match
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Match match = PotPvPSlave.getInstance().getMatchHandler().getMatchPlaying(event.getPlayer().getUniqueId());

        if (match == null) {
            return;
        }

        KitHandler kitHandler = PotPvPSlave.getInstance().getKitHandler();
        KitType kitType = match.getKitType();

        PlayerUtils.resetInventory(event.getPlayer(), GameMode.SURVIVAL);
        int slot = 2; // start in 3rd slot

        for (Kit kit : kitHandler.getKits(event.getPlayer().getUniqueId(), kitType)) {
            event.getPlayer().getInventory().setItem(slot, kit.createSelectionItem());
            slot += 2;
        }

        event.getPlayer().getInventory().setItem(0, kitType.createDefaultKit().createSelectionItem());
    }

    /**
     * Don't let players drop their kit selection items via death
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Match match = PotPvPSlave.getInstance().getMatchHandler().getMatchPlaying(event.getEntity().getUniqueId());

        if (match == null) {
            return;
        }

        KitHandler kitHandler = PotPvPSlave.getInstance().getKitHandler();
        KitType kitType = match.getKitType();

        for (Kit kit : kitHandler.getKits(event.getEntity().getUniqueId(), kitType)) {
            event.getDrops().remove(kit.createSelectionItem());
        }

        event.getDrops().remove(kitType.createDefaultKit().createSelectionItem());
    }

    /**
     * Give players their kits upon right click
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Match match = PotPvPSlave.getInstance().getMatchHandler().getMatchPlaying(event.getPlayer().getUniqueId());

        if (match == null) {
            return;
        }

        DetailedKitType detailedKitType = match.getDetailedKitType();
        List<Kit> kits = PotPvPSlave.getInstance().getKitHandler().getKits(event.getPlayer().getUniqueId(), match.getKitType());
        Kit defaultKit = match.getKitType().createDefaultKit();

        for (Kit kit : kits) {
            if (kit.isSelectionItem(event.getItem())) {
                kit.apply(event.getPlayer(), detailedKitType);
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You equipped your \"" + kit.getName() + "\" " + kit.getType().getName() + " Kit.");
                break;
            }
        }

        if (defaultKit.isSelectionItem(event.getItem())) {
            defaultKit.apply(event.getPlayer(), detailedKitType);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You equipped the default kit for " + defaultKit.getType().getName() + ".");
        }
    }

}