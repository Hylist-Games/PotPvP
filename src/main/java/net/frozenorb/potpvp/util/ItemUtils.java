package net.frozenorb.potpvp.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.function.Predicate;

import lombok.experimental.UtilityClass;

// TODO: Consider merging this into qLib's ItemUtils class.
@UtilityClass
public final class ItemUtils {

    /**
     * Checks if a {@link ItemStack} is an instant heal potion (if its type is {@link PotionType#INSTANT_HEAL})
     */
    public static final Predicate<ItemStack> INSTANT_HEAL_POTION_PREDICATE = item -> {
        if (item.getType() != Material.POTION) {
            return false;
        }

        PotionType potionType = Potion.fromItemStack(item).getType();
        return potionType == PotionType.INSTANT_HEAL;
    };

    /**
     * Checks if a {@link ItemStack} is a debuff (if its type is contained in {@link PotionUtils#DEBUFF_POTION_TYPES})
     */
    public static final Predicate<ItemStack> DEBUFF_POTION_PREDICATE = item -> {
        if (item.getType() != Material.POTION) {
            return false;
        }

        PotionType potionType = Potion.fromItemStack(item).getType();
        return PotionUtils.DEBUFF_POTION_TYPES.contains(potionType);
    };

    /**
     * Checks if a {@link ItemStack} is edible (if its type passes {@link Material#isEdible()})
     */
    public static final Predicate<ItemStack> EDIBLE_PREDICATE = item -> item.getType().isEdible();

    /**
     * Returns the total amount of items matching the predicate provided. It should be noted
     * that for each match the return value is increment by each ItemStack's amount, NOT 1.
     *
     * @param items ItemStack array to scan
     * @param predicate The predicate which will be applied to each non-null temStack.
     * @return The summed amount of all items which matched the predicate, or 0 if {@code items} was null.
     */
    public static int countAmountMatching(ItemStack[] items, Predicate<ItemStack> predicate) {
        if (items == null) {
            return 0;
        }

        int amountMatching = 0;

        for (ItemStack item : items) {
            if (item != null && predicate.test(item)) {
                amountMatching += item.getAmount();
            }
        }

        return amountMatching;
    }

}