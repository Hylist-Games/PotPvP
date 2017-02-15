package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchState;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

// the name of this listener is definitely kind of iffy (as it's really any non-IN_PROGRESS match),
// but any other ideas I had were even less descriptive
public final class MatchCountdownListener implements Listener {

    /**
     * Prevents damage in non IN_PROGRESS matches
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getEntity().getUniqueId());

        if (match != null && match.getState() != MatchState.IN_PROGRESS) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents launching projects in non IN_PROGRESS matches
     * Attempts to return enderpearls to their thrower
     */
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();

        if (!(shooter instanceof Player)) {
            return;
        }

        Player shooterPlayer = (Player) shooter;
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(shooterPlayer);

        if (match != null && match.getState() != MatchState.IN_PROGRESS) {
            event.setCancelled(true);

            // for enderpearls only we give back the pearl
            // as cancelling the event won't do that for us
            if (event.getEntityType() == EntityType.ENDER_PEARL) {
                shooterPlayer.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                shooterPlayer.updateInventory();
            }
        }
    }

    /**
     * Lock player healh in place while their match isn't in progress
     * This primarily exists to prevent healing in the ENDING state
     */
    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getEntity().getUniqueId());

        if (match != null && match.getState() != MatchState.IN_PROGRESS) {
            event.setCancelled(true);
        }
    }

}