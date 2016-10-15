package net.frozenorb.potpvp.kit;

import com.google.common.collect.ImmutableList;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;

import net.frozenorb.potpvp.PotPvPSI;
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

final class PlayerKitStore {

    private UUID player;
    private final Map<KitType, Map<Integer, Kit>> kits = new ConcurrentHashMap<>();

    PlayerKitStore() {}

    PlayerKitStore(UUID player) {
        this.player = player;
    }

    Optional<Kit> getFavoriteKit(KitType kitType) {
        Map<Integer, Kit> kitSlots = kits.get(kitType);

        if (kitSlots == null) {
            return Optional.empty();
        }

        for (Kit possibleFavorite : kitSlots.values()) {
            if (possibleFavorite.getType() == kitType && possibleFavorite.isFavorite()) {
                return Optional.of(possibleFavorite);
            }
        }

        return Optional.empty();
    }

    List<Kit> getKits(KitType kitType) {
        if (kits.containsKey(kitType)) {
            return ImmutableList.copyOf(kits.get(kitType).values());
        } else {
            return ImmutableList.of();
        }
    }

    Optional<Kit> getKit(KitType kitType, int slot) {
        Map<Integer, Kit> kitSlots = kits.get(kitType);

        if (kitSlots != null) {
            return Optional.ofNullable(kitSlots.get(slot));
        } else {
            return Optional.empty();
        }
    }

    Kit setKit(KitType kitType, int slot, Kit kit) {
        Map<Integer, Kit> kitSlots = kits.get(kitType);

        if (kitSlots == null) {
            kitSlots = new ConcurrentHashMap<>();
            kits.put(kitType, kitSlots);
        }

        kitSlots.put(slot, kit);
        saveKitsAsync();
        return kit;
    }

    Kit setDefaultKit(KitType kitType, int slot) {
        Kit createdKit = Kit.ofDefaultKit(
            kitType,
            "Kit " + slot,
            getKits(kitType).isEmpty() // this kit is our favorite if it's our only kit
        );

        return setKit(kitType, slot, createdKit);
    }

    Optional<Kit> removeKit(KitType kitType, int slot) {
        Map<Integer, Kit> kitSlots = kits.get(kitType);

        if (kitSlots != null) {
            Kit removed = kitSlots.remove(slot);

            saveKitsAsync();
            return Optional.ofNullable(removed);
        } else {
            return Optional.empty();
        }
    }

    // this should be private, see KitHandler::saveKitsAsync for details.
    void saveKitsAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            MongoCollection<Document> playerKitsCollection = MongoUtils.getCollection("PlayerKits");
            Document json = Document.parse(qLib.PLAIN_GSON.toJson(PlayerKitStore.this));

            playerKitsCollection.updateOne(new Document("player", player.toString()), json, new UpdateOptions().upsert(true));
        });
    }

}