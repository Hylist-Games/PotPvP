package net.frozenorb.potpvp.command;

import java.io.IOException;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.elo.repository.EloRepository;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.qlib.command.Command;
import net.minecraft.util.com.google.common.collect.ImmutableSet;

public final class EloConvertCommand {

    @Command(names = {"eloconvert"}, permission = "op")
    public static void eloconvert(Player sender) {
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();

        EloRepository repository = PotPvPSI.getInstance().getEloHandler().getEloRepository();

        for (int i = 0; i < offlinePlayers.length; i++) {
            OfflinePlayer target = offlinePlayers[i];

            if (i % 100 == 0) {
                sender.sendMessage(ChatColor.GREEN + "Converting: " + i + "/" + offlinePlayers.length);
            }

            try {
                Map<KitType, Integer> map = repository.loadElo(ImmutableSet.of(target.getUniqueId()));
                repository.saveElo(ImmutableSet.of(target.getUniqueId()), map);
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "An error occured.");
                e.printStackTrace();
            }
        }
    }

}
