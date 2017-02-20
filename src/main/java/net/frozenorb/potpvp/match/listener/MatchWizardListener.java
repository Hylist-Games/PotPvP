package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.util.FireworkEffectPlayer;
import net.frozenorb.qlib.qLib;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public final class MatchWizardListener implements Listener {

    private final FireworkEffectPlayer fireworkEffectPlayer = new FireworkEffectPlayer();

    @EventHandler(priority = EventPriority.MONITOR)
    // no ignoreCancelled = true because right click on air
    // events are by default cancelled (wtf Bukkit)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.DIAMOND_HOE || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getPlayer();
        Match match = matchHandler.getMatchPlaying(player);

        if (match == null || !match.getKitType().getId().equals("WIZARD")) {
            return;
        }

        FireworkEffect effect = FireworkEffect.builder()
            .withColor(Color.BLUE)
            .with(FireworkEffect.Type.BALL_LARGE)
            .build();

        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setVelocity(arrow.getVelocity().multiply(2));

        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {
                if (ticks++ >= 100) {
                    cancel();
                    return;
                }

                if (arrow.isDead() || arrow.isOnGround()) {
                    for (Entity entity : arrow.getNearbyEntities(4, 4, 4)) {
                        if (match.getTeam(entity.getUniqueId()) != null) {
                            entity.setVelocity(entity.getLocation().toVector().subtract(arrow.getLocation().toVector()));
                        }
                    }

                    arrow.remove();
                    cancel();
                } else {
                    try {
                        fireworkEffectPlayer.playFirework(arrow.getWorld(), arrow.getLocation(), effect);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        }.runTaskTimer(PotPvPSI.getInstance(), 1L, 1L);
    }

}