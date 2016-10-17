package net.frozenorb.potpvp.party.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.party.Party;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class PartyChatListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.getMessage().startsWith("@")) {
            return;
        }

        Player player = event.getPlayer();
        String message = event.getMessage().substring(1);
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

        if (party == null) {
            player.sendMessage(ChatColor.RED + "You aren't in a party!");
            event.setCancelled(true);
            return;
        }

        ChatColor prefixColor = party.isLeader(player.getUniqueId()) ? ChatColor.AQUA : ChatColor.LIGHT_PURPLE;
        party.message(prefixColor.toString() + ChatColor.BOLD + "[P] " + player.getName() + ": " + ChatColor.LIGHT_PURPLE + message);

        PotPvPSI.getInstance().getLogger().info("[Party Chat] " + player.getName() + ": " + message);
        event.setCancelled(true);
    }

}