package net.frozenorb.potpvp.kittype;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

/**
 * Contains metadata about a KitType. Metadata, in this context, means
 * anything relating to a KitType that is meant to be edited and stored
 * in a database, instead of in enum definitions. These values can also
 * be edited live, where as values defined directly in KitType (ex debuffs)
 * cannot be edited live.
 */
public final class KitTypeMeta {

    /**
     * Items which will be available for players to grab in the kit
     * editor, when making kits for this kit type.
     * @see net.frozenorb.potpvp.kit.menu.EditKitMenu
     */
    @Getter @Setter private ItemStack[] editorItems = new ItemStack[0];

    /**
     * The armor that will be applied to players for this kit type.
     * Currently players are not allowed to edit their armor, they are
     * always given this armor.
     */
    @Getter @Setter private ItemStack[] defaultArmor = new ItemStack[0];

    /**
     * The default inventory that will be applied to players for this kit type.
     * Players are always allowed to rearange this inventory, so this only serves
     * as a default (in contrast to defaultArmor)
     */
    @Getter @Setter private ItemStack[] defaultInventory = new ItemStack[0];

    /**
     * Determines if players are allowed to spawn in items while editing their kits.
     * For some kit types (ex archer and axe) players can only rearange items in kits,
     * whereas some kit types (ex HCTeams and soup) allow spawning in items as well.
     */
    @Getter @Setter private boolean editorSpawnAllowed = true;

}