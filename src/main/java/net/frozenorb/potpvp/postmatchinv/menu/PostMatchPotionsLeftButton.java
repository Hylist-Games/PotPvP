package net.frozenorb.potpvp.postmatchinv.menu;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.frozenorb.qlib.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

final class PostMatchPotionsLeftButton extends Button {

    private final String playerName;
    private final int potionsRemaining;

    PostMatchPotionsLeftButton(String playerName, int potionsRemaining) {
        this.playerName = Preconditions.checkNotNull(playerName, "playerName");
        this.potionsRemaining = potionsRemaining;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN.toString() + potionsRemaining + " Health Pots";
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack item = super.getButtonItem(player);
        item.setDurability((short) 16421);
        return item;
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            ChatColor.YELLOW + playerName + " had " + potionsRemaining + " health potion" + (potionsRemaining == 1 ? "" : "s") + " left."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.POTION;
    }

    @Override
    public int getAmount(Player player) {
        return potionsRemaining;
    }

}