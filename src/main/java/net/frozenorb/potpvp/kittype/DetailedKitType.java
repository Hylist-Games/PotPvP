package net.frozenorb.potpvp.kittype;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A DetailedKitType is an extension of a KitType, adding in data about if debuffs
 * are allowed. ({@link DebuffSetting}) A DetailedKitType can be thought of as similar
 * to a Pair<KitType, DebuffSetting>. See {@link net.frozenorb.potpvp.kittype} for
 * an explanation of KitType and DetailedKitType.
 *
 * This class acts like a pseudo enum by exposing methods like {@link DetailedKitType#values()}.
 */
@EqualsAndHashCode
public final class DetailedKitType {

    // used to cache objects to avoid excessive object creation
    private static final Table<KitType, DebuffSetting, DetailedKitType> cache = HashBasedTable.create();

    /**
     * Underlying KitType which will be extended with additional data
     */
    @Getter private final KitType kitType;

    /**
     * If debuffs are allowed in this DetailedKitType, used in combination with KitType
     * to represent a detailed KitType. See {@link net.frozenorb.potpvp.kittype} for
     * an explanation of KitType and DetailedKitType.
     */
    @Getter private final DebuffSetting debuffSetting;

    /**
     * Factor method to access DetailedKitTypes.
     * @param kitType KitType to be used in resulting DetailedKitType
     * @param debuffSetting DebuffSetting to be used in resulting DetailedKitType.
     * @return DetailedKitType representing KitType and DebuffSetting passed in.
     */
    public static DetailedKitType of(KitType kitType, DebuffSetting debuffSetting) {
        // 'flatten' any KitTypes not supporting debuffs into DebuffSetting.DISALLOWED,
        // to ensure we don't create multiple DetailedKitTypes for the same (to the user) combination.
        if (!kitType.isSupportsDebuffs()) {
            debuffSetting = DebuffSetting.DISALLOWED;
        }

        DetailedKitType detailedKitType = cache.get(kitType, debuffSetting);

        if (detailedKitType == null) {
            detailedKitType = new DetailedKitType(kitType, debuffSetting);
            cache.put(kitType, debuffSetting, detailedKitType);
        }

        return detailedKitType;
    }

    private DetailedKitType(KitType kitType, DebuffSetting debuffSetting) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.debuffSetting = Preconditions.checkNotNull(debuffSetting, "debuffSetting");
    }

    /**
     * Gets full display name for this DetailedKitType. If the underlying KitType supports
     * debuffs, the result will be the underlying KitType's display name with " (Debuffs)"
     * or " (No Debuffs)" suffixed. If the underlying KitType does not support debuffs, the
     * result will be identical to calling {@link KitType#getDisplayName()}.
     * @return full display name for this DetailedKitType
     */
    public String getDisplayName() {
        return fillDisplayName("Debuffs", "No Debuffs");
    }

    /**
     * Gets shortened display name for this DetailedKitType. If the underlying KitType supports
     * debuffs, the result will be the underlying KitType's display name with " (D)"
     * or " (ND)" suffixed. If the underlying KitType does not support debuffs, the
     * result will be identical to calling {@link KitType#getDisplayName()}.
     * @return shortened display name for this DetailedKitType
     */
    public String getShortDisplayName() {
        return fillDisplayName("D", "ND");
    }

    /**
     * Gets all possible DetailedKitType valeus, keeping into account which KitTypes do/do not
     * support debuffs.
     * @return All possible DetailedKitType values.
     */
    public static DetailedKitType[] values() {
        // we don't just pull from our cache in case of the (rare)
        // event where an entry hasn't been populated yet
        List<DetailedKitType> result = new ArrayList<>();

        for (KitType kit : KitType.values()) {
            result.add(kit.withoutDebuffs());

            if (kit.isSupportsDebuffs()) {
                result.add(kit.withDebuffs());
            }
        }

        return result.toArray(new DetailedKitType[result.size()]);
    }

    private String fillDisplayName(String debuffsAllowed, String debuffsDisallowed) {
        if (kitType.isSupportsDebuffs()) {
            String debuffString = debuffSetting == DebuffSetting.ALLOWED ? debuffsAllowed : debuffsDisallowed;
            return kitType.getDisplayName() + ChatColor.GREEN + " (" + debuffString + ")";
        } else {
            // if debuffs aren't supported just use normal display name
            return kitType.getDisplayName();
        }
    }

}