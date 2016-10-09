package net.frozenorb.potpvp.setting;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import lombok.Getter;

/**
 * Called when a player updates a setting value.
 */
public final class SettingUpdateEvent extends PlayerEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    /**
     * The setting that was updated
     */
    @Getter private final Setting setting;

    /**
     * The new state of the setting
     */
    @Getter private final boolean enabled;

    // not public so we can only call this event from SettingHandler
    SettingUpdateEvent(Player player, Setting setting, boolean enabled) {
        super(player);

        this.setting = Preconditions.checkNotNull(setting, "setting");
        this.enabled = enabled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}