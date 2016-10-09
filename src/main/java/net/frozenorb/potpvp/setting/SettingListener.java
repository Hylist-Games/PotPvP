package net.frozenorb.potpvp.setting;

import com.google.common.base.Preconditions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

final class SettingListener implements Listener {

    private final SettingHandler settingHandler;

    SettingListener(SettingHandler settingHandler) {
        this.settingHandler = Preconditions.checkNotNull(settingHandler, "settingHandler");
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        settingHandler.loadSettings(event.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        settingHandler.unloadSettings(event.getPlayer().getUniqueId());
    }

}