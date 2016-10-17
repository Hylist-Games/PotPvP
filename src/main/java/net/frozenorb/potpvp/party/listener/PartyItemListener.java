package net.frozenorb.potpvp.party.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.party.PartyItems;
import net.frozenorb.potpvp.party.command.PartyInfoCommand;
import net.frozenorb.potpvp.party.command.PartyLeaveCommand;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class PartyItemListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item.isSimilar(PartyItems.LEAVE_PARTY_ITEM)) {
            event.setCancelled(true);
            PartyLeaveCommand.partyLeave(player);
        } else {
            Party party = partyHandler.getParty(player);

            if (party == null) {
                return;
            }

            ItemStack icon = PartyItems.icon(party);

            if (icon.isSimilar(item)) {
                event.setCancelled(true);
                PartyInfoCommand.partyInfo(player, player);
            }
        }
    }

}