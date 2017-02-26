package net.frozenorb.potpvp.postmatchinv;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.kittype.KitType;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.UUID;

import lombok.Getter;

public final class PostMatchPlayer {

    @Getter private final UUID playerUuid;
    @Getter private final ItemStack[] armor;
    @Getter private final ItemStack[] inventory;
    @Getter private final List<PotionEffect> potionEffects;
    @Getter private final int hunger;
    @Getter private final double health; // out of 10
    @Getter private final transient KitType kitType;

    public PostMatchPlayer(Player player, KitType kitType) {
        this.playerUuid = player.getUniqueId();
        this.armor = player.getInventory().getArmorContents();
        this.inventory = player.getInventory().getContents();
        this.potionEffects = ImmutableList.copyOf(player.getActivePotionEffects());
        this.hunger = player.getFoodLevel();
        this.health = Math.round(player.getHealth()) / 2D;
        this.kitType = kitType;
    }

}