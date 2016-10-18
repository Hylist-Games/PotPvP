package net.frozenorb.potpvp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class ChatListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (player.hasMetadata("HydrogenPrefix")) {
            String prefix = player.getMetadata("HydrogenPrefix").get(0).asString();
            event.setFormat(prefix + "%s: %s");
        } else {
            event.setFormat("%s: %s");
        }
    }

}