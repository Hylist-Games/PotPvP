package net.frozenorb.potpvp.kittype;

/**
 * Represents if debuffs (debuff splash potions, see {@link net.frozenorb.potpvp.util.PotionUtils#DEBUFF_POTION_TYPES})
 * are allowed. Used instead of a boolean to increase readability. (Java doesn't have type aliases.)
 */
public enum DebuffSetting {

    ALLOWED,
    DISALLOWED,

}