package net.frozenorb.potpvp.setting.listener;

import net.frozenorb.potpvp.PotPvPSI;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class SettingLoadListener implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        PotPvPSI.getInstance().getSettingHandler().loadSettings(event.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PotPvPSI.getInstance().getSettingHandler().unloadSettings(event.getPlayer().getUniqueId());
    }

}