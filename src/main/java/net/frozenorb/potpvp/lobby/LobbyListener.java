package net.frozenorb.potpvp.lobby;

import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.qlib.util.PlayerUtils;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

final class LobbyListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasItem() && event.getAction().name().contains("RIGHT")) {
            if (event.getItem().isSimilar(LobbyItems.LEAVE_PARTY_ITEM)) {
                //PartyLeaveCommand.partyLeave(event.getPlayer());
            } else if (event.getItem().isSimilar(LobbyItems.OTHER_PARTIES_ITEM)) {
                //new OtherPartiesMenu().openMenu(event.getPlayer());
            } else if (event.getItem().isSimilar(LobbyItems.PENDING_INVITES_ITEM)) {
                //new PendingPartyMatchInvitesMenu().openMenu(event.getPlayer());
            } else if (event.getItem().isSimilar(LobbyItems.REQUEST_REMATCH_ITEM)) {
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
            } else if (event.getItem().isSimilar(LobbyItems.ACCEPT_REMATCH_ITEM)) {
                /*RematchData rematchData = PotPvPLobby.getInstance().getRematchHandler().getRematchData(event.getPlayer().getUniqueId());

                if (rematchData != null) {
                    AcceptCommand.accept(event.getPlayer(), rematchData.getTarget());
                }*/
            } else if (event.getItem().isSimilar(LobbyItems.EVENTS_ITEM)) {
                event.getPlayer().sendMessage(ChatColor.RED + "Events are not yet completed! They will be done soon!");
            } else if (event.getItem().isSimilar(LobbyItems.START_TEAM_SPLIT_ITEM)) {
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
            } else if (event.getItem().getType() == Material.POTION) {
                event.setCancelled(true); // don't allow potions
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        InventoryUtils.resetInventory(event.getPlayer());
        event.setRespawnLocation(event.getPlayer().getWorld().getSpawnLocation());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().isOp() || event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().isOp() || event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.setDeathMessage(null);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.teleport(player.getWorld().getSpawnLocation());
        player.setGameMode(GameMode.SURVIVAL);

        PlayerUtils.resetInventory(player);
        InventoryUtils.resetInventory(player);
    }

}