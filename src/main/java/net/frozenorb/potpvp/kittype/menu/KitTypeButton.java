package net.frozenorb.potpvp.kittype.menu;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.util.Callback;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class KitTypeButton extends Button {

    private final KitType kitType;
    private final Callback<KitType> callback;

    KitTypeButton(KitType kitType, Callback<KitType> callback) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.callback = Preconditions.checkNotNull(callback, "callback");
    }

    @Override
    public String getName(Player player) {
        return kitType.getDisplayColor() + ChatColor.BOLD.toString() + kitType.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.YELLOW + "Click here to select " + ChatColor.BOLD + kitType.getName() + ChatColor.YELLOW + "."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return kitType.getIcon().getItemType();
    }

    @Override
    public byte getDamageValue(Player player) {
        return kitType.getIcon().getData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        callback.callback(kitType);
    }

}