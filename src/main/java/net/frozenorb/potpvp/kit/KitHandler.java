package net.frozenorb.potpvp.kit;

import com.mongodb.client.MongoCollection;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kit.listener.KitItemListener;
import net.frozenorb.potpvp.kit.listener.KitLoadListener;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.util.MongoUtils;
import net.frozenorb.qlib.qLib;

import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class KitHandler {

    public static final int KITS_PER_TYPE = 4;

    private final Map<UUID, PlayerKitStore> kitStores = new ConcurrentHashMap<>();

    public KitHandler() {
        Bukkit.getPluginManager().registerEvents(new KitItemListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new KitLoadListener(), PotPvPSI.getInstance());
    }

    public Optional<Kit> getFavoriteKit(UUID playerUuid, KitType kitType) {
        return kitStores.get(playerUuid).getFavoriteKit(kitType);
    }

    public List<Kit> getKits(UUID playerUuid, KitType kitType) {
        return kitStores.get(playerUuid).getKits(kitType);
    }

    public Optional<Kit> getKit(UUID playerUuid, KitType kitType, int slot) {
        return kitStores.get(playerUuid).getKit(kitType, slot);
    }

    public Kit setKit(UUID playerUuid, KitType kitType, int slot, Kit kit) {
        return kitStores.get(playerUuid).setKit(kitType, slot, kit);
    }

    public Kit setDefaultKit(UUID playerUuid, KitType kitType, int slot) {
        return kitStores.get(playerUuid).setDefaultKit(kitType, slot);
    }

    public Optional<Kit> removeKit(UUID playerUuid, KitType kitType, int slot) {
        return kitStores.get(playerUuid).removeKit(kitType, slot);
    }

    /* Deprecated because kit handling / saving should be entirely contained in `PlayerKitStore`s,
       however since `Kit`s are mutable we allow this method to trigger a save (since changes to `Kit`s
       can't be detected by `PlayerKitStore`s. Solutions to this include, but are not limited to,
        * Making `Kit`s immutable
        * Having `Kit`s hold a reference to their PlayerKitStore so they can trigger a save independently
          however when we make a Kit and build it manually (ex PlayerKitStore::setDefaultKit) we have to
          disable such functionality.
     */
    @Deprecated
    public void saveKitsAsync(UUID playerUuid) {
        kitStores.get(playerUuid).saveKitsAsync();
    }

    public void loadKits(UUID playerUuid) {
        MongoCollection<Document> playerKitsCollection = MongoUtils.getCollection("PlayerKits");
        Document playerData = playerKitsCollection.find(new Document("player", playerUuid.toString())).first();

        if (playerData != null) {
            PlayerKitStore playerKitStore = qLib.PLAIN_GSON.fromJson(playerData.toString(), PlayerKitStore.class);
            kitStores.put(playerUuid, playerKitStore);
        } else {
            kitStores.put(playerUuid, new PlayerKitStore(playerUuid));
        }
    }

    public void unloadKits(UUID playerUuid) {
        kitStores.remove(playerUuid);
    }

}