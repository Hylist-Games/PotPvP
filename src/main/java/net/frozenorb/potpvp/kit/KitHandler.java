package net.frozenorb.potpvp.kit;

import com.google.common.collect.ImmutableList;

import com.mongodb.client.MongoCollection;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kit.listener.KitEditorListener;
import net.frozenorb.potpvp.kit.listener.KitItemListener;
import net.frozenorb.potpvp.kit.listener.KitLoadListener;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.util.MongoUtils;
import net.frozenorb.qlib.qLib;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class KitHandler {

    public static final String MONGO_COLLECTION_NAME = "kits";
    public static final int KITS_PER_TYPE = 4;

    private final Map<UUID, List<Kit>> kitData = new ConcurrentHashMap<>();

    public KitHandler() {
        Bukkit.getPluginManager().registerEvents(new KitEditorListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new KitItemListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new KitLoadListener(), PotPvPSI.getInstance());
    }

    public List<Kit> getKits(Player player, KitType kitType) {
        return kitData.getOrDefault(player.getUniqueId(), ImmutableList.of())
            .stream()
            .filter(k -> k.getType() == kitType)
            .collect(Collectors.toList());
    }

    public Optional<Kit> getKit(Player player, KitType kitType, int slot) {
        return kitData.getOrDefault(player.getUniqueId(), ImmutableList.of())
            .stream()
            .filter(k -> k.getType() == kitType && k.getSlot() == slot)
            .findFirst();
    }

    public Kit saveDefaultKit(Player player, KitType kitType, int slot) {
        Kit created = Kit.ofDefaultKit(kitType, "Kit " + slot, slot);

        kitData.computeIfAbsent(player.getUniqueId(), i -> new ArrayList<>()).add(created);
        saveKitsAsync(player);

        return created;
    }

    public void removeKit(Player player, KitType kitType, int slot) {
        boolean removed = kitData.computeIfAbsent(player.getUniqueId(), i -> new ArrayList<>())
                .removeIf(k -> k.getType() == kitType && k.getSlot() == slot);

        if (removed) {
            saveKitsAsync(player);
        }
    }

    public void saveKitsAsync(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            MongoCollection<Document> collection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
            String kitJson = qLib.PLAIN_GSON.toJson(kitData.getOrDefault(player.getUniqueId(), ImmutableList.of()));

            Document query = new Document("_id", player.getUniqueId().toString());
            Document kitUpdate = new Document("$set", new Document("kits", kitJson));

            collection.updateOne(query, kitUpdate, MongoUtils.UPSERT_OPTIONS);
        });
    }

    public void loadKits(UUID playerUuid) {
        MongoCollection<Document> collection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
        Document playerKits = collection.find(new Document("_id", playerUuid.toString())).first();

        if (playerKits != null) {
            String kitJson = playerKits.get("kits", Document.class).toJson();
            Type listKit = new TypeToken<List<Kit>>() {}.getType();

            kitData.put(playerUuid, qLib.PLAIN_GSON.fromJson(kitJson, listKit));
        }
    }

    public void unloadKits(Player player) {
        kitData.remove(player.getUniqueId());
    }

}