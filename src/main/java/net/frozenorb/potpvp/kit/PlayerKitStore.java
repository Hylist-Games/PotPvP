package net.frozenorb.potpvp.kit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.KitType;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

final class PlayerKitStore {

    private UUID player;
    private final Map<KitType, Map<Integer, Kit>> kits = new ConcurrentHashMap<>();

    PlayerKitStore() {} // for gson

    PlayerKitStore(UUID player) {
        this.player = Preconditions.checkNotNull(player, "player");
    }

    List<Kit> getKits(KitType kitType) {
        Map<Integer, Kit> slots = kits.getOrDefault(kitType, ImmutableMap.of());
        return ImmutableList.copyOf(slots.values());
    }

    Kit getKit(KitType kitType, int slot) {
        Map<Integer, Kit> slots = kits.getOrDefault(kitType, ImmutableMap.of());
        return slots.get(slot);
    }

    Kit saveDefaultKit(KitType kitType, int slot) {
        Kit createdKit = Kit.ofDefaultKit(kitType, "Kit " + slot);
        Map<Integer, Kit> slots = kits.get(kitType);

        if (slots == null) {
            slots = new ConcurrentHashMap<>();
            kits.put(kitType, slots);
        }

        slots.put(slot, createdKit);
        PotPvPSI.getInstance().getKitHandler().saveKitsAsync(player);
        return createdKit;
    }

    void removeKit(KitType kitType, int slot) {
        Map<Integer, Kit> slots = kits.get(kitType);

        if (slots != null) {
            slots.remove(slot);
            PotPvPSI.getInstance().getKitHandler().saveKitsAsync(player);
        }
    }

}