package net.frozenorb.potpvp.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.VisibilityUtils;
import net.frozenorb.qlib.command.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public final class SilentCommand {

    @Command(names = {"silent"}, permission = "potpvp.silent")
    public static void silent(Player sender) {
        if (sender.hasMetadata("ModMode")) {
            sender.removeMetadata("ModMode", PotPvPSI.getInstance());
            sender.removeMetadata("invisible", PotPvPSI.getInstance());

            sender.sendMessage(ChatColor.RED + "Silent mode disabled.");
        } else {
            sender.setMetadata("ModMode", new FixedMetadataValue(PotPvPSI.getInstance(), true));
            sender.setMetadata("invisible", new FixedMetadataValue(PotPvPSI.getInstance(), true));
            
            sender.sendMessage(ChatColor.GREEN + "Silent mode enabled.");
        }

        VisibilityUtils.updateVisibility(sender);
    }

}