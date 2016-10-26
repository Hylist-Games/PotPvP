package net.frozenorb.potpvp.rematch.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.duels.DuelAcceptCommand;
import net.frozenorb.potpvp.duels.DuelInviteCommand;
import net.frozenorb.potpvp.rematch.RematchData;
import net.frozenorb.potpvp.rematch.RematchHandler;
import net.frozenorb.potpvp.rematch.RematchItems;
import net.frozenorb.potpvp.util.InventoryUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class RematchItemListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        RematchHandler rematchHandler = PotPvPSI.getInstance().getRematchHandler();
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item.isSimilar(RematchItems.REQUEST_REMATCH_ITEM)) {
            event.setCancelled(true);

            RematchData rematchData = rematchHandler.getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());
                DuelInviteCommand.duel(player, target, rematchData.getKitType());
                
                InventoryUtils.resetInventoryDelayed(player);
                InventoryUtils.resetInventoryDelayed(target);
            }
        } else if (item.isSimilar(RematchItems.SENT_REMATCH_ITEM)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You have already sent a rematch request.");
        } else if (item.isSimilar(RematchItems.ACCEPT_REMATCH_ITEM)) {
            event.setCancelled(true);

            RematchData rematchData = rematchHandler.getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());
                DuelAcceptCommand.accept(player, target);
            }
        }
    }

}