package net.frozenorb.potpvp.elo;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.elo.listener.EloLoadListener;
import net.frozenorb.potpvp.elo.listener.EloUpdateListener;
import net.frozenorb.potpvp.elo.repository.EloRepository;
import net.frozenorb.potpvp.elo.repository.MongoEloRepository;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.party.Party;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class EloHandler {

    private static final int DEFAULT_ELO = 1_500;

    private final Map<Set<UUID>, Map<KitType, Integer>> eloData = new ConcurrentHashMap<>();
    private final EloRepository eloRepository;

    public EloHandler() {
        Bukkit.getPluginManager().registerEvents(new EloLoadListener(this), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new EloUpdateListener(this, new EloCalculator(
            50,
            7,
            25,
            7,
            25
        )), PotPvPSI.getInstance());

        eloRepository = new MongoEloRepository();
    }

    public int getElo(Player player, KitType kitType) {
        return getElo(ImmutableSet.of(player.getUniqueId()), kitType);
    }

    public void setElo(Player player, KitType kitType, int newElo) {
        setElo(ImmutableSet.of(player.getUniqueId()), kitType, newElo);
    }

    public int getElo(Set<UUID> playerUuids, KitType kitType) {
        Map<KitType, Integer> partyElo = eloData.getOrDefault(playerUuids, ImmutableMap.of());
        return partyElo.getOrDefault(kitType, DEFAULT_ELO);
    }

    public void setElo(Set<UUID> playerUuids, KitType kitType, int newElo) {
        Map<KitType, Integer> partyElo = eloData.computeIfAbsent(playerUuids, i -> new ConcurrentHashMap<>());
        partyElo.put(kitType, newElo);

        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            try {
                eloRepository.saveElo(playerUuids, partyElo);
            } catch (IOException ex) {
                // just log, nothing else to do.
                ex.printStackTrace();
            }
        });
    }

    public void loadElo(Set<UUID> playerUuids) {
        Map<KitType, Integer> partyElo;

        try {
            partyElo = new ConcurrentHashMap<>(eloRepository.loadElo(playerUuids));
        } catch (IOException ex) {
            // just print + return an empty map, this will cause us
            // to fall back to default values.
            ex.printStackTrace();
            partyElo = new ConcurrentHashMap<>();
        }

        eloData.put(playerUuids, partyElo);
    }

    public void unloadElo(Set<UUID> playerUuids) {
        eloData.remove(playerUuids);
    }

}