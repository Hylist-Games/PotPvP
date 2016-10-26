package net.frozenorb.potpvp.kit;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.util.ItemUtils;
import net.frozenorb.qlib.util.PlayerUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.Setter;

public final class Kit {

    @Getter @Setter private String name;
    @Getter @Setter private KitType type;
    @Getter @Setter private ItemStack[] inventoryContents;

    public static Kit ofDefaultKit(KitType kitType) {
        return ofDefaultKit(kitType, "Default Kit");
    }

    public static Kit ofDefaultKit(KitType kitType, String name) {
        Kit kit = new Kit();

        kit.setName(name);
        kit.setType(kitType);
        kit.setInventoryContents(kitType.getMeta().getDefaultInventory());

        return kit;
    }

    public void apply(Player player) {
        PlayerUtils.resetInventory(player);

        // we don't let players actually customize their armor, we just apply default
        player.getInventory().setArmorContents(type.getMeta().getDefaultArmor());
        player.getInventory().setContents(inventoryContents);

        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), player::updateInventory, 1L);
    }

    public int countHeals() {
        return ItemUtils.countAmountMatching(inventoryContents, ItemUtils.INSTANT_HEAL_POTION_PREDICATE);
    }

    public int countDebuffs() {
        return ItemUtils.countAmountMatching(inventoryContents, ItemUtils.DEBUFF_POTION_PREDICATE);
    }

    public int countFood() {
        return ItemUtils.countAmountMatching(inventoryContents, ItemUtils.EDIBLE_PREDICATE);
    }

    public int countPearls() {
        return ItemUtils.countAmountMatching(inventoryContents, v -> v.getType() == Material.ENDER_PEARL);
    }

    // we use this method instead of .toSelectableBook().isSimilar()
    // to avoid the slight performance overhead of constructing
    // that itemstack every time
    public boolean isSelectionItem(ItemStack itemStack) {
        if (itemStack.getType() != Material.ENCHANTED_BOOK) {
            return false;
        }

        ItemMeta meta = itemStack.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.YELLOW.toString() + ChatColor.BOLD + name);
    }

    public ItemStack createSelectionItem() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + name);

        item.setItemMeta(itemMeta);
        return item;
    }

}