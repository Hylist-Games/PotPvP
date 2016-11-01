package net.frozenorb.potpvp.kit.menu.manage;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.kit.Kit;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

final class KitTypeInfoButton extends Button {

    private final KitType kit;

    KitTypeInfoButton(KitType kit) {
        this.kit = Preconditions.checkNotNull(kit, "kit");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN.toString() + ChatColor.BOLD + "Editing: " + ChatColor.AQUA + kit.getDisplayName();
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
                ChatColor.GRAY + "You are editing '" + kit.getDisplayName() + "'"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.NAME_TAG;
    }

}