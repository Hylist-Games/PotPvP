package net.frozenorb.potpvp.matchstats;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.event.MatchEndEvent;
import net.frozenorb.potpvp.match.event.MatchTerminateEvent;
import net.frozenorb.qlib.util.PlayerUtils;

import org.bson.Document;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.UUID;

final class MatchStatsListener implements Listener {

    private final MatchStatsHandler matchStatsHandler;
    
    MatchStatsListener(MatchStatsHandler matchStatsHandler) {
        this.matchStatsHandler = matchStatsHandler;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getEntity());

        if (match != null) {
            UUID playerUuid = event.getEntity().getUniqueId();
            matchStatsHandler.saveStatistics(playerUuid, match);
        }
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        matchStatsHandler.finalizeStatistics(event.getMatch());
    }

    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        Match match = event.getMatch();
        Document databaseEntry = event.getDatabaseEntry();

        matchStatsHandler.contributeDatabaseInfo(match, databaseEntry);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntityType() != EntityType.ARROW) {
            return;
        }

        ProjectileSource source = event.getEntity().getShooter();

        if (source instanceof Player) {
            Player sourcePlayer = (Player) source;
            matchStatsHandler.recordBowShot(sourcePlayer.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        if (event.getAnimationType() == PlayerAnimationType.ARM_SWING) {
            matchStatsHandler.recordSwing(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player damagerPlayer = PlayerUtils.getDamageSource(event.getDamager());

        if (damagerPlayer == null) {
            return;
        }

        if (event.getDamager() instanceof Player) {
            matchStatsHandler.recordSwingHit(damagerPlayer.getUniqueId());
        } else if (event.getDamager() instanceof Arrow) {
            matchStatsHandler.recordBowHit(damagerPlayer.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        matchStatsHandler.resetPlayerStats(event.getPlayer().getUniqueId());
    }

}