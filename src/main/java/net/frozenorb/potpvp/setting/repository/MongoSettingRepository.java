package net.frozenorb.potpvp.setting.repository;

import com.google.common.collect.ImmutableMap;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.setting.Setting;

import org.bson.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MongoSettingRepository implements SettingRepository {

    private static final String MONGO_COLLECTION_NAME = "PlayerSettings";
    private static final UpdateOptions UPSERT_OPTIONS = new UpdateOptions().upsert(true);

    private final MongoCollection<Document> settingsCollection;

    public MongoSettingRepository(MongoDatabase mongoDatabase) {
        this.settingsCollection = mongoDatabase.getCollection(MONGO_COLLECTION_NAME);
    }

    @Override
    public Map<Setting, Boolean> loadSettings(UUID playerUuid) throws IOException {
        Document settingsDocument;

        try {
            settingsDocument = settingsCollection.find(buildQuery(playerUuid)).first();
        } catch (MongoException ex) {
            throw new IOException(ex);
        }

        // no settings is okay, just return an empty map
        if (settingsDocument == null) {
            return ImmutableMap.of();
        }

        Document rawSettings = settingsDocument.get("settings", Document.class);
        Map<Setting, Boolean> parsedSettings = new HashMap<>();

        rawSettings.forEach((rawSetting, value) -> {
            try {
                parsedSettings.put(Setting.valueOf(rawSetting), (Boolean) value);
            } catch (Exception ex) {
                PotPvPSI.getInstance().getLogger().info("Failed to load setting " + rawSetting + " (value=" + value + ") for " + playerUuid + ".");
            }
        });

        return ImmutableMap.copyOf(parsedSettings);
    }

    @Override
    public void saveSettings(UUID playerUuid, Map<Setting, Boolean> settings) throws IOException {
        Document settingsDocument = new Document();

        settings.forEach((setting, value) -> {
            settingsDocument.put(setting.name(), value);
        });

        Document update = new Document("$set", new Document("settings", settingsDocument));

        try {
            settingsCollection.updateOne(buildQuery(playerUuid), update, UPSERT_OPTIONS);
        } catch (MongoException ex) {
            throw new IOException(ex);
        }
    }

    private Document buildQuery(UUID playerUuid) {
        return new Document("_id", playerUuid.toString());
    }

}