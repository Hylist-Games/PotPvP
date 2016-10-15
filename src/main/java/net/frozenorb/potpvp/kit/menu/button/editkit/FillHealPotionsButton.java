package net.frozenorb.potpvp.kit.menu.button.editkit;

import com.google.common.collect.ImmutableList;

import net.frozenorb.qlib.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class FillHealPotionsButton extends Button {

    private final short durability;

    public FillHealPotionsButton(short durability) {
        this.durability = durability;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.BLUE + "Fill Empty Space";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
                "",
                ChatColor.LIGHT_PURPLE + "Fill your empty inventory space",
                ChatColor.LIGHT_PURPLE + "wih Splash Health Potions."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.POTION;
    }

    // getDamageValue is for the data, not durability
    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack superItem = super.getButtonItem(player);

        superItem.setDurability(durability);

        return superItem;
    }

    @Override
    public void clicked(final Player player, int slot, ClickType clickType) {
        ItemStack potion = new ItemStack(Material.POTION);

        potion.setDurability(durability);

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            player.getInventory().addItem(potion);
        }
    }

}