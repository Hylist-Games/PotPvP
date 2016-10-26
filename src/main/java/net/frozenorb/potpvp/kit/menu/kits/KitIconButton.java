package net.frozenorb.potpvp.kit.menu.kits;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kit.Kit;
import net.frozenorb.potpvp.kit.KitHandler;
import net.frozenorb.potpvp.kit.menu.editkit.EditKitMenu;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.qlib.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class KitIconButton extends Button {

    private final Kit kit;
    private final KitType kitType;
    private final int slot;

    KitIconButton(Kit kit, KitType kitType, int slot) {
        this.kit = kit;
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.slot = slot;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN.toString() + ChatColor.BOLD + (kit == null ? "Create Kit" : kit.getName());
    }

    @Override
    public List<String> getDescription(Player player) {
        if (kit != null) {
            return ImmutableList.of(
                "",
                ChatColor.GREEN + "Heals: " + ChatColor.WHITE + kit.countHeals(),
                ChatColor.RED + "Debuffs: " + ChatColor.WHITE + kit.countDebuffs()
            );
        } else {
            return ImmutableList.of();
        }
    }

    @Override
    public Material getMaterial(Player player) {
        return kit != null ? Material.DIAMOND_SWORD : Material.STONE_SWORD;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        Kit kit = this.kit;

        if (kit == null) {
            KitHandler kitHandler = PotPvPSI.getInstance().getKitHandler();
            kit = kitHandler.saveDefaultKit(player.getUniqueId(), kitType, this.slot);
        }

        new EditKitMenu(kit).openMenu(player);
    }

}