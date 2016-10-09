package net.frozenorb.potpvp.setting;

import com.mongodb.client.MongoDatabase;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.setting.repository.MongoSettingRepository;
import net.frozenorb.potpvp.setting.repository.SettingRepository;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SettingHandler {

    // we previously had a PlayerSettings class which acted as a type alias for
    // a Map<Setting, Boolean> but the client interface is ultimately the same and
    // this is slightly easier for us to work with
    private final Map<UUID, Map<Setting, Boolean>> settingsData = new ConcurrentHashMap<>();
    private final SettingRepository settingRepository;

    public SettingHandler() {
        Bukkit.getPluginManager().registerEvents(new SettingListener(this), PotPvPSI.getInstance());

        MongoDatabase mongoDatabase = PotPvPSI.getInstance().getMongoDatabase();
        settingRepository = new MongoSettingRepository(mongoDatabase);
    }

    /**
     * Retrieves the value of a setting for the player provided, falling back to the
     * setting's default value if the player hasn't updated the setting or the player's
     * settings failed to load.
     *
     * @param playerUuid The player to look up settings for
     * @param setting The Setting to look up the value of
     * @return If the setting is, after considered defaults and player customizations, enabled.
     */
    public boolean getSetting(UUID playerUuid, Setting setting) {
        Map<Setting, Boolean> playerSettings = settingsData.get(playerUuid);
        return playerSettings.getOrDefault(setting, setting.getDefaultValue());
    }

    /**
     * Updates the value of a setting for the player provided. Automatically handles
     * calling {@link SettingUpdateEvent}s and saving the changes in a database.
     *
     * @param playerUuid The player to update settings for
     * @param setting The Setting to update the value of
     * @param enabled If the setting should be enabled
     */
    public void updateSetting(UUID playerUuid, Setting setting, boolean enabled) {
        Map<Setting, Boolean> playerSettings = settingsData.get(playerUuid);
        playerSettings.put(setting, enabled);

        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            try {
                // we call .get() again instead of referencing playerSettings to avoid a
                // problem w/ out of order execution if players change settings rapidly
                settingRepository.saveSettings(playerUuid, settingsData.get(playerUuid));
            } catch (IOException ex) {
                // just log, nothing else to do.
                ex.printStackTrace();
            }
        });

        // call SettingUpdateEvent if possible
        Player bukkitPlayer = Bukkit.getPlayer(playerUuid);

        if (bukkitPlayer != null) {
            SettingUpdateEvent settingUpdateEvent = new SettingUpdateEvent(bukkitPlayer, setting, enabled);
            Bukkit.getPluginManager().callEvent(settingUpdateEvent);
        }
    }

    void loadSettings(UUID playerUuid) {
        Map<Setting, Boolean> playerSettings;

        try {
            playerSettings = new ConcurrentHashMap<>(settingRepository.loadSettings(playerUuid));
        } catch (IOException ex) {
            // just print + return an empty map, this will cause us
            // to fall back to default values.
            ex.printStackTrace();
            playerSettings = new ConcurrentHashMap<>();
        }

        settingsData.put(playerUuid, playerSettings);
    }

    void unloadSettings(UUID playerUuid) {
        settingsData.remove(playerUuid);
    }

}