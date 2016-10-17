package net.frozenorb.potpvp.lobby.listener;

import net.frozenorb.potpvp.lobby.LobbyItems;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class LobbyItemListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item.isSimilar(LobbyItems.OTHER_PARTIES_ITEM)) {
            event.setCancelled(true);
            //new OtherPartiesMenu().openMenu(event.getPlayer());
        } else if (item.isSimilar(LobbyItems.PENDING_INVITES_ITEM)) {
            event.setCancelled(true);
            //new PendingPartyMatchInvitesMenu().openMenu(event.getPlayer());
        } else if (item.isSimilar(LobbyItems.REQUEST_REMATCH_ITEM)) {
            event.setCancelled(true);
            /*RematchData rematchData = PotPvPLobby.getInstance().getRematchHandler().getRematchData(event.getPlayer().getUniqueId());

            if (rematchData != null && PotPvPValidation.canRematch(event.getPlayer())) {
                if (PotPvPLobby.getInstance().getMatchHandler().isInMatch(rematchData.getTarget())) {
                    event.getPlayer().sendMessage(ChatColor.RED + "That player is currently in a match!");
                    return;
                } else if (!PotPvPLobby.getInstance().getPlayerLocator().getAllPlayers().contains(rematchData.getTarget())) {
                    event.getPlayer().sendMessage(ChatColor.RED + FrozenUUIDCache.name(rematchData.getTarget()) + " is not online!");
                    return;
                }

                if (PotPvPLobby.getInstance().getInviteHandler().hasPlayerInvite(rematchData.getSender(), rematchData.getTarget())) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You have already sent " + FrozenUUIDCache.name(rematchData.getTarget()) + " a rematch request!");
                    return;
                }

                rematchData.setSent(true);

                DetailedKitType kitType = rematchData.getKitType();

                PlayerMatchInvite invite = PlayerMatchInvite.createMatchInvite(rematchData.getSender(), rematchData.getTarget(), kitType, true);
                PotPvPLobby.getInstance().getInviteHandler().registerInvitation(invite);

                if (invite.getTarget() == null) {
                    FrozenXPacketHandler.sendToAll(MatchInvitePacket.from(invite));
                }
            }*/
        } else if (item.isSimilar(LobbyItems.SENT_REMATCH_ITEM)) {
            event.setCancelled(true);
            /*???*/
        } else if (item.isSimilar(LobbyItems.ACCEPT_REMATCH_ITEM)) {
            event.setCancelled(true);
            /*RematchData rematchData = PotPvPLobby.getInstance().getRematchHandler().getRematchData(event.getPlayer().getUniqueId());

            if (rematchData != null) {
                AcceptCommand.accept(event.getPlayer(), rematchData.getTarget());
            }*/
        } else if (item.isSimilar(LobbyItems.EVENTS_ITEM)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Events are not yet completed! They will be done soon!");
        } else if (item.isSimilar(LobbyItems.START_TEAM_SPLIT_ITEM)) {
            event.setCancelled(true);
            /*Party party = PotPvPLobby.getInstance().getPartyHandler().getLocalParty(event.getPlayer());

            if (party != null) {
                if (!party.getLeader().equals(event.getPlayer().getUniqueId())) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You aren't the leader of your party.");
                    return;
                }

                if (PotPvPValidation.canStartTeamSplit(party)) {
                    party.startTeamSplit(event.getPlayer());
                }
            }*/
        }
    }

}