package net.frozenorb.potpvp.tab;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.profile.Profile;
import net.frozenorb.hydrogen.rank.Rank;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.qlib.tab.TabLayout;
import net.frozenorb.qlib.util.PlayerUtils;
import net.frozenorb.qlib.util.UUIDUtils;

public class AlternateOnlinePlayersLayoutProvider implements Listener, BiConsumer<Player, TabLayout> {

    private Map<UUID, String> playersMap = generateNewTreeMap();
    
    public AlternateOnlinePlayersLayoutProvider() {
        Bukkit.getPluginManager().registerEvents(this, PotPvPSI.getInstance());
        Bukkit.getScheduler().runTaskTimerAsynchronously(PotPvPSI.getInstance(), this::rebuildCache, 0, 1 * 60 * 20);
    }

    @Override
    public void accept(Player player, TabLayout tabLayout) {
        tabLayout.set(1, 0, "&d&lVeltPvP");

        int x = 0;
        int y = 2;

        boolean isStaff = player.hasPermission("basic.staff");
        for (Entry<UUID, String> entry : playersMap.entrySet()) {
            if (x == 3) {
                x = 0;
                y++;
            }

            if (entry.getValue() == null) {
                continue;
            }

            if (19 <= y) {
                break;
            }

            Player otherPlayer = Bukkit.getPlayer(entry.getKey());
            if (otherPlayer.hasMetadata("ModMode")) {
                if (!isStaff) {
                    continue;
                }

                tabLayout.set(x++, y, "* " + entry.getValue(), getPing(entry.getKey()));
            } else {
                tabLayout.set(x++, y, entry.getValue(), getPing(entry.getKey()));
            }
        }

        tabLayout.set(0, 18, "", 1);
        tabLayout.set(1, 18, "", 1);
        tabLayout.set(2, 18, "", 1);
        tabLayout.set(0, 19, "&6&leu.veltpvp.com");
        tabLayout.set(1, 19, "&6&lveltpvp.com");
        tabLayout.set(2, 19, "&6&lau.veltpvp.com");
    }

    
    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        playersMap.put(event.getPlayer().getUniqueId(), getName(event.getPlayer().getUniqueId()));
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playersMap.remove(event.getPlayer().getUniqueId());
    }
    
    private void rebuildCache() {
        TreeMap<UUID, String> newTreeMap = generateNewTreeMap();
        
        Bukkit.getOnlinePlayers().forEach(player -> {
            newTreeMap.put(player.getUniqueId(), getName(player.getUniqueId()));
        });
        
        this.playersMap = newTreeMap;
    }
    
    private String getName(UUID uuid) {
        Profile profile = Hydrogen.getInstance().getProfileHandler().getProfile(uuid).orElse(null);
        if (profile == null) {
            return UUIDUtils.name(uuid);
        }

        Rank bestDisplayRank = profile.getBestDisplayRank();
        if (bestDisplayRank == null) {
            return UUIDUtils.name(uuid);
        }

        return bestDisplayRank.getGameColor() + UUIDUtils.name(uuid);
    }

    public int getPing(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        return player == null ? -1 : Math.max(((PlayerUtils.getPing(player) + 5) / 10) * 10, 1);
    }
    
    private TreeMap<UUID, String> generateNewTreeMap() {
        return new TreeMap<UUID, String>(new Comparator<UUID>() {
            
            @Override
            public int compare(UUID first, UUID second) {
                Profile firstProfile = Hydrogen.getInstance().getProfileHandler().getProfile(first).orElse(null);
                Profile secondProfile = Hydrogen.getInstance().getProfileHandler().getProfile(second).orElse(null);
                
                if (firstProfile != null && secondProfile != null) {
                    int compare = Integer.compare(secondProfile.getBestDisplayRank().getDisplayWeight(), firstProfile.getBestDisplayRank().getDisplayWeight());
                    if (compare == 0) {
                        return tieBreaker(first, second);
                    }

                    return compare;
                } else if (firstProfile != null && secondProfile == null) {
                    return -1;
                } else if (firstProfile == null && secondProfile != null) {
                    return 1;
                } else {
                    return tieBreaker(first, second);
                }
            }
            
        });
    }

    private int tieBreaker(UUID first, UUID second) {
        String firstName = UUIDUtils.name(first);
        String secondName = UUIDUtils.name(second);

        return firstName.compareTo(secondName);
    }
    
}
