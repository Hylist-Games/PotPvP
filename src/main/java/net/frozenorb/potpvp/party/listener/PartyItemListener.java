package net.frozenorb.potpvp.party.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.party.PartyItems;
import net.frozenorb.potpvp.party.command.PartyFfaCommand;
import net.frozenorb.potpvp.party.command.PartyInfoCommand;
import net.frozenorb.potpvp.party.command.PartyLeaveCommand;
import net.frozenorb.potpvp.party.command.PartyTeamSplitCommand;
import net.frozenorb.potpvp.party.menu.otherparties.OtherPartiesMenu;

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
        } else if (item.isSimilar(PartyItems.START_TEAM_SPLIT_ITEM)) {
            event.setCancelled(true);
            PartyTeamSplitCommand.partyTeamSplit(player);
        } else if (item.isSimilar(PartyItems.START_FFA_ITEM)) {
            event.setCancelled(true);
            PartyFfaCommand.partyFfa(player);
        } else if (item.isSimilar(PartyItems.OTHER_PARTIES_ITEM)) {
            event.setCancelled(true);
            new OtherPartiesMenu().openMenu(player);
        } else if (item.getType() == PartyItems.ICON_TYPE) {
            // we just check for the same type (not isSimilar because
            // of a different title) to avoid running this code
            // whenever any item is right clicked
            Party party = partyHandler.getParty(player);

            if (party != null && PartyItems.icon(party).isSimilar(item)) {
                event.setCancelled(true);
                PartyInfoCommand.partyInfo(player, player);
            }
        }
    }

}