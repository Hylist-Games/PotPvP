package net.frozenorb.potpvp.kit.menu.button.editkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kit.menu.KitsMenu;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.qlib.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public final class CancelKitEditButton extends Button {

    private final KitType kitType;

    public CancelKitEditButton(KitType kitType) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Cancel";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.YELLOW + "Click this to abort editing your kit,",
            ChatColor.YELLOW + "and return to the kit menu."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player player) {
        return DyeColor.RED.getWoolData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        InventoryUtils.resetInventory(player);

        new KitsMenu(kitType).openMenu(player);
    }

}