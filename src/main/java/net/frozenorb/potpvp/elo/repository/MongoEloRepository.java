package net.frozenorb.potpvp.elo.repository;

import com.google.common.collect.ImmutableMap;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.util.MongoUtils;

import org.bson.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class MongoEloRepository implements EloRepository {

    private static final String MONGO_COLLECTION_NAME = "elo";

    public MongoEloRepository() {
        MongoUtils.getCollection(MONGO_COLLECTION_NAME).createIndex(new Document("players", 1));
    }

    @Override
    public Map<KitType, Integer> loadElo(Set<UUID> playerUuids) throws IOException {
        MongoCollection<Document> partyEloCollection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
        Document query = new Document("players", playerUuids);

        try {
            Document eloDocument = partyEloCollection.find(query).first();

            // no elo is okay, just return an empty map
            if (eloDocument == null) {
                return ImmutableMap.of();
            }

            Document rawElo = eloDocument.get("elo", Document.class);
            Map<KitType, Integer> parsedElo = new HashMap<>();

            rawElo.forEach((kit, value) -> {
                KitType parsed = KitType.byId(kit);

                if (parsed != null) {
                    parsedElo.put(parsed, (Integer) value);
                } else {
                    PotPvPSI.getInstance().getLogger().info("Failed to load elo kit=" + kit + ", value=" + value + ".");
                }
            });

            return ImmutableMap.copyOf(parsedElo);
        } catch (MongoException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void saveElo(Set<UUID> playerUuids, Map<KitType, Integer> elo) throws IOException {
        Document document = new Document();
        elo.forEach((kit, value) -> document.put(kit.getId(), value));

        Document update = new Document("$set", new Document("elo", document));

        try {
            MongoUtils.getCollection(MONGO_COLLECTION_NAME).updateOne(
                new Document("players", playerUuids),
                update,
                MongoUtils.UPSERT_OPTIONS // creates document if it doesn't exist
            );
        } catch (MongoException ex) {
            throw new IOException(ex);
        }
    }

}