package net.frozenorb.potpvp.postmatchinv.menu;

import com.google.common.collect.ImmutableList;

import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.util.UUIDUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

final class PostMatchHealsLeftButton extends Button {

    private final String playerName;
    private final int healsRemaining;
    private final boolean pots;

    PostMatchHealsLeftButton(UUID player, int healsRemaining, boolean pots) {
        this.playerName = UUIDUtils.name(player);
        this.healsRemaining = healsRemaining;
        this.pots = pots;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN.toString() + healsRemaining + " " + (pots ? "Health Pots" : "Soup");
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack item = super.getButtonItem(player);

        if (pots) {
            item.setDurability((short) 16421);
        }

        return item;
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            ChatColor.YELLOW + playerName + " had " + healsRemaining + " " + (pots ? "health potion" : "soup") + (healsRemaining == 1 ? "" : "s") + " left."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return pots ? Material.POTION : Material.MUSHROOM_SOUP;
    }

    @Override
    public int getAmount(Player player) {
        return healsRemaining;
    }

}