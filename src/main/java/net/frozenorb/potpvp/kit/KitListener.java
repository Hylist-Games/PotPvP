package net.frozenorb.potpvp.kit;

import com.google.common.base.Preconditions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

final class KitListener implements Listener {

    private final KitHandler kitHandler;

    KitListener(KitHandler kitHandler) {
        this.kitHandler = Preconditions.checkNotNull(kitHandler, "kitHandler");
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        kitHandler.loadKits(event.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        kitHandler.unloadKits(event.getPlayer().getUniqueId());
    }

    // TODO: OPEN KIT EDITOR!!!!

}