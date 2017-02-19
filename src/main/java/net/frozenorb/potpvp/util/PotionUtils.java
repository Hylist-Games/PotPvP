package net.frozenorb.potpvp.util;

import com.google.common.collect.ImmutableSet;

import org.bukkit.potion.PotionType;

import java.util.Set;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PotionUtils {

    /**
     * {@link PotionType}s which are considered debuffs. This list is not sourced from
     * anywhere.
     */
    public static final Set<PotionType> DEBUFF_POTION_TYPES = ImmutableSet.of(
        PotionType.WEAKNESS,
        PotionType.SLOWNESS,
        PotionType.POISON,
        PotionType.INSTANT_DAMAGE
    );

}