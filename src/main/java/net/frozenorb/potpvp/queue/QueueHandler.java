package net.frozenorb.potpvp.queue;

import net.frozenorb.gcd.bukkit.GcdBukkit;
import net.frozenorb.gcd.client.ex.ServerRequestException;
import net.frozenorb.gcd.db.model.PartyModel;
import net.frozenorb.gcd.http.match.JoinQueueMessage;
import net.frozenorb.gcd.http.match.JoinQueueResponse;
import net.frozenorb.potpvp.PotPvPMessages;
import net.frozenorb.potpvp.PotPvPPlugin;
import net.frozenorb.potpvp.kittype.DetailedKitType;
import net.frozenorb.potpvp.party.PartyUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class QueueHandler {

    public QueueHandler() {
        Bukkit.getPluginManager().registerEvents(new QueueItemListener(this), PotPvPPlugin.getInstance());
    }

    public void joinQuickMatchQueue(Player player, DetailedKitType kitType) {
        try {
            JoinQueueMessage message = new JoinQueueMessage(kitType.getEncodedName(), player.getUniqueId());
            GcdBukkit.instance().client().send(message, JoinQueueResponse.class);

            player.sendMessage(ChatColor.GREEN + "You are now queued for " + kitType.getDisplayName() + ChatColor.GREEN + ".");
        } catch (ServerRequestException ex) {
            player.sendMessage(PotPvPMessages.COULD_NOT_CONTACT_MATCH_SERVER);
            PotPvPPlugin.getInstance().getInventoryResetHandler().resetInventoryLater(player, 1L);
        }
    }

    public void joinQuickMatchQueue(PartyModel party, DetailedKitType kitType) {
        try {
            JoinQueueMessage message = new JoinQueueMessage(kitType.getEncodedName(), party.leader());
            GcdBukkit.instance().client().send(message, JoinQueueResponse.class);

            PartyUtils.sendMessage(party, ChatColor.GREEN + "Your party is now queued for " + kitType.getDisplayName() + ChatColor.GREEN + ".");
        } catch (ServerRequestException ex) {
            PartyUtils.sendMessage(party, PotPvPMessages.COULD_NOT_CONTACT_MATCH_SERVER);
            PartyUtils.resetInventoryLater(party, 1L);
        }
    }

    public void leaveQuickMatchQueue(Player player) {
        try {
            // TODO
            player.sendMessage(ChatColor.GREEN + "You are no longer queued.");
        } catch (ServerRequestException ex) {
            player.sendMessage(PotPvPMessages.COULD_NOT_CONTACT_MATCH_SERVER);
        }
    }

    public void leaveQuickMatchQueue(PartyModel party) {
        try {
            // TODO
            PartyUtils.sendMessage(party, ChatColor.GREEN + "Your party is no longer queued.");
        } catch (ServerRequestException ex) {
            PartyUtils.sendMessage(party, PotPvPMessages.COULD_NOT_CONTACT_MATCH_SERVER);
        }
    }

    public boolean isInQuickMatchQueue(Player player) {
        return soloQueue.containsKey(player.getUniqueId()) && isQueued(player.getUniqueId(), kitType, false);
    }

    public boolean isInQuickMatchQueue(PartyModel party) {
        return soloQueue.containsKey(player.getUniqueId()) && isQueued(player.getUniqueId(), kitType, false);
    }

}