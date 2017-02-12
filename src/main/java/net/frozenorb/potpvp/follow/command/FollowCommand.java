package net.frozenorb.potpvp.follow.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.follow.FollowHandler;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class FollowCommand {

    @Command(names={"follow"}, permission="")
    public static void follow(Player sender, @Param(name="target") Player target) {
        if (!PotPvPValidation.canFollowSomeone(sender)) {
            return;
        }

        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
        UUID alreadyFollowing = followHandler.getFollowing(sender).orElse(null);

        if (alreadyFollowing != null) {
            sender.sendMessage(ChatColor.BLUE + "You're already following " + ChatColor.YELLOW + UUIDUtils.name(alreadyFollowing) + ChatColor.BLUE + "!");
            return;
        } else if (sender == target) {
            sender.sendMessage(ChatColor.RED + "No, you can't follow yourself.");
            return;
        }

        followHandler.startFollowing(sender, target);
    }

}