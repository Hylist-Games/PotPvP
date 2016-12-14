package net.frozenorb.potpvp.match;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.Arena;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.match.event.MatchCountdownStartEvent;
import net.frozenorb.potpvp.match.event.MatchEndEvent;
import net.frozenorb.potpvp.match.event.MatchSpectatorJoinEvent;
import net.frozenorb.potpvp.match.event.MatchSpectatorLeaveEvent;
import net.frozenorb.potpvp.match.event.MatchStartEvent;
import net.frozenorb.potpvp.match.event.MatchTerminateEvent;
import net.frozenorb.potpvp.postmatchinv.PostMatchPlayer;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.util.MongoUtils;
import net.frozenorb.potpvp.util.VisibilityUtils;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.util.PlayerUtils;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;

public final class Match {

    private static final int MATCH_END_DELAY_SECONDS = 2;

    @Getter private final String id;
    @Getter private final KitType kitType;
    @Getter private final Arena arena;
    @Getter private final List<MatchTeam> teams; // immutable so @Getter is ok
    private final Map<UUID, PostMatchPlayer> postMatchPlayers = new HashMap<>();
    private final Set<UUID> spectators = new HashSet<>();

    @Getter private MatchTeam winner;
    @Getter private MatchEndReason endReason;
    @Getter private MatchState state;
    @Getter private Instant startedAt;
    @Getter private Instant endedAt;

    // we track if matches should give a rematch diamond manually. previouly
    // we just checked if both teams had 1 player on them, but this wasn't
    // always accurate. Scenarios like a team split of a 3 man team (with one
    // sitting out) would get treated as a 1v1 when calculating rematches.
    // https://github.com/FrozenOrb/PotPvP-SI/issues/19
    @Getter private boolean allowRematches;

    public Match(KitType kitType, Arena arena, List<MatchTeam> teams, boolean allowRematches) {
        this.id = UUID.randomUUID().toString();
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.arena = Preconditions.checkNotNull(arena, "arena");
        this.teams = ImmutableList.copyOf(teams);
        this.allowRematches = allowRematches;
    }

    void startCountdown() {
        state = MatchState.COUNTDOWN;

        Map<UUID, Match> playingCache = PotPvPSI.getInstance().getMatchHandler().getPlayingMatchCache();
        Set<Player> updateVisiblity = new HashSet<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            MatchTeam team = getTeam(player.getUniqueId());

            if (team == null) {
                continue;
            }

            playingCache.put(player.getUniqueId(), this);

            Location spawn = team == teams.get(0) ? arena.getTeam1Spawn() : arena.getTeam2Spawn();

            player.teleport(spawn);
            player.getInventory().setHeldItemSlot(0);

            FrozenNametagHandler.reloadPlayer(player);
            FrozenNametagHandler.reloadOthersFor(player);

            updateVisiblity.add(player);
            PlayerUtils.resetInventory(player, GameMode.SURVIVAL);
        }

        // we wait to update visibility until everyone's been put in the player cache
        // then we update vis, otherwise we'll see 'partial' views of the match
        updateVisiblity.forEach(VisibilityUtils::updateVisibility);

        Bukkit.getPluginManager().callEvent(new MatchCountdownStartEvent(this));

        new BukkitRunnable() {

            int countdownTimeRemaining = 5;

            public void run() {
                if (state != MatchState.COUNTDOWN) {
                    cancel();
                    return;
                }

                if (countdownTimeRemaining == 0) {
                    playSoundAll(Sound.NOTE_PLING, 2F);
                    startMatch();
                    return; // so we don't send '0...' message
                } else if (countdownTimeRemaining <= 3) {
                    playSoundAll(Sound.NOTE_PLING, 1F);
                }

                messageAll(ChatColor.YELLOW.toString() + countdownTimeRemaining + "...");
                countdownTimeRemaining--;
            }

        }.runTaskTimer(PotPvPSI.getInstance(), 0L, 20L);
    }

    private void startMatch() {
        state = MatchState.IN_PROGRESS;
        startedAt = Instant.now();

        messageAll(ChatColor.GREEN + "Match started.");
        Bukkit.getPluginManager().callEvent(new MatchStartEvent(this));
    }

    public void endMatch(MatchEndReason reason) {
        state = MatchState.ENDING;
        endedAt = Instant.now();
        endReason = reason;

        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID playerUuid = player.getUniqueId();
                MatchTeam team = getTeam(playerUuid);

                if (team != null) {
                    postMatchPlayers.computeIfAbsent(playerUuid, v -> new PostMatchPlayer(player));
                }
            }

            messageAll(ChatColor.RED + "Match ended.");
            Bukkit.getPluginManager().callEvent(new MatchEndEvent(this));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        int delayTicks = MATCH_END_DELAY_SECONDS * 20;
        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), this::terminateMatch, delayTicks);
    }

    // we try-catch a lot of code here to ensure a match never fails to end
    private void terminateMatch() {
        state = MatchState.TERMINATED;

        // if the match ends before the countdown ends
        // we have to set this to avoid a NPE in Date#from
        if (startedAt == null) {
            startedAt = Instant.now();
        }

        // if endedAt wasn't set before (if terminateMatch was called directly)
        // we want to make sure we set an ending time.
        if (endedAt == null) {
            endedAt = Instant.now();
        }

        try {
            Document document = Document.parse(qLib.PLAIN_GSON.toJson(this));

            // rename 'id' to '_id' (for mongo)
            document.put("_id", document.remove("id"));

            // overwrite fields which would normally serialize too much or improperly
            document.put("winner", winner != null ? winner.getId() : null);
            document.put("arena", arena.getSchematic());
            document.put("startedAt", Date.from(startedAt));
            document.put("endedAt", Date.from(endedAt));

            Bukkit.getPluginManager().callEvent(new MatchTerminateEvent(this, document));
            Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
                MongoUtils.getCollection(MatchHandler.MONGO_COLLECTION_NAME).insertOne(document);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        LobbyHandler lobbyHandler = PotPvPSI.getInstance().getLobbyHandler();

        Map<UUID, Match> playingCache = matchHandler.getPlayingMatchCache();
        Map<UUID, Match> spectateCache = matchHandler.getSpectatingMatchCache();

        arenaHandler.releaseArena(arena);
        matchHandler.removeMatch(this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerUuid = player.getUniqueId();
            MatchTeam team = getTeam(playerUuid);

            if (team != null || spectators.contains(playerUuid)) {
                playingCache.remove(playerUuid);
                spectateCache.remove(playerUuid);

                try {
                    lobbyHandler.returnToLobby(player);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public Set<UUID> getSpectators() {
        return ImmutableSet.copyOf(spectators);
    }

    public Map<UUID, PostMatchPlayer> getPostMatchPlayers() {
        return ImmutableMap.copyOf(postMatchPlayers);
    }

    private void checkEnded() {
        List<MatchTeam> teamsAlive = new ArrayList<>();

        for (MatchTeam team : teams) {
            if (!team.getAliveMembers().isEmpty()) {
                teamsAlive.add(team);
            }
        }

        if (teamsAlive.size() == 1) {
            this.winner = teamsAlive.get(0);
            endMatch(MatchEndReason.ENEMIES_ELIMINATED);
        }
    }

    public boolean isSpectator(UUID uuid) {
        return spectators.contains(uuid);
    }

    public void addSpectator(Player player, Player target) {
        addSpectator(player, target, true);
    }

    public void addSpectator(Player player, Player target, boolean teleportPlayer) {
        Map<UUID, Match> spectateCache = PotPvPSI.getInstance().getMatchHandler().getSpectatingMatchCache();

        spectateCache.put(player.getUniqueId(), this);
        spectators.add(player.getUniqueId());

        if (teleportPlayer) {
            player.teleport(target != null ? target.getLocation() : arena.getSpectatorSpawn());
        }

        player.getInventory().setHeldItemSlot(0);

        /*
        MatchSpectator specData = match.getSpectators().get(player.getUniqueId());

        if (!specData.isHidden() && match.getState() == MatchState.IN_PROGRESS) {
            SettingHandler settingHandler = PotPvPSlave.getInstance().getSettingHandler();

            for (Player onlinePlayer : PotPvPSlave.getInstance().getServer().getOnlinePlayers()) {
                if (onlinePlayer == player) {
                    continue;
                }

                boolean sameMatch = match.isSpectator(onlinePlayer.getUniqueId()) || match.getCurrentTeam(onlinePlayer) != null;
                boolean spectatorMessagesEnabled = settingHandler.isSettingEnabled(onlinePlayer.getUniqueId(), Setting.SHOW_SPECTATOR_JOIN_MESSAGES);

                if (sameMatch && spectatorMessagesEnabled) {
                    onlinePlayer.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " is now spectating.");
                }
            }
        }
        */

        FrozenNametagHandler.reloadPlayer(player);
        FrozenNametagHandler.reloadOthersFor(player);

        VisibilityUtils.updateVisibility(player);
        PlayerUtils.resetInventory(player, GameMode.CREATIVE);
        InventoryUtils.resetInventoryDelayed(player);

        Bukkit.getPluginManager().callEvent(new MatchSpectatorJoinEvent(player, this));
    }

    public void removeSpectator(Player player) {
        Map<UUID, Match> spectateCache = PotPvPSI.getInstance().getMatchHandler().getSpectatingMatchCache();

        spectateCache.remove(player.getUniqueId());
        spectators.remove(player.getUniqueId());

        PotPvPSI.getInstance().getLobbyHandler().returnToLobby(player);
        Bukkit.getPluginManager().callEvent(new MatchSpectatorLeaveEvent(player, this));
    }

    public void markDead(Player player) {
        MatchTeam team = getTeam(player.getUniqueId());

        if (team != null) {
            Map<UUID, Match> playingCache = PotPvPSI.getInstance().getMatchHandler().getPlayingMatchCache();

            team.markDead(player.getUniqueId());
            playingCache.remove(player.getUniqueId());

            postMatchPlayers.put(player.getUniqueId(), new PostMatchPlayer(player));
            checkEnded();
        }
    }

    public MatchTeam getTeam(UUID playerUuid) {
        for (MatchTeam team : teams) {
            if (team.isAlive(playerUuid)) {
                return team;
            }
        }

        return null;
    }

    public MatchTeam getPreviousTeam(UUID playerUuid) {
        for (MatchTeam team : teams) {
            if (team.getAllMembers().contains(playerUuid)) {
                return team;
            }
        }

        return null;
    }

    /**
     * Sends a basic chat message to all alive participants and spectators
     * @param message the message to send
     */
    public void messageAll(String message) {
        messageAlive(message);
        messageSpectators(message);
    }

    /**
     * Plays a sound for all alive participants and spectators
     * @param sound the Sound to play
     * @param pitch the pitch to play the provided sound at
     */
    public void playSoundAll(Sound sound, float pitch) {
        playSoundAlive(sound, pitch);
        playSoundSpectators(sound, pitch);
    }

    /**
     * Sends a basic chat message to all spectators
     * @param message the message to send
     */
    public void messageSpectators(String message) {
        for (UUID spectator : spectators) {
            Player spectatorBukkit = Bukkit.getPlayer(spectator);

            if (spectatorBukkit != null) {
                spectatorBukkit.sendMessage(message);
            }
        }
    }

    /**
     * Plays a sound for all spectators
     * @param sound the Sound to play
     * @param pitch the pitch to play the provided sound at
     */
    public void playSoundSpectators(Sound sound, float pitch) {
        for (UUID spectator : spectators) {
            Player spectatorBukkit = Bukkit.getPlayer(spectator);

            if (spectatorBukkit != null) {
                spectatorBukkit.playSound(spectatorBukkit.getEyeLocation(), sound, 10F, pitch);
            }
        }
    }

    /**
     * Sends a basic chat message to all alive participants
     * @see MatchTeam#messageAlive(String)
     * @param message the message to send
     */
    public void messageAlive(String message) {
        for (MatchTeam team : teams) {
            team.messageAlive(message);
        }
    }

    /**
     * Plays a sound for all alive participants
     * @param sound the Sound to play
     * @param pitch the pitch to play the provided sound at
     */
    public void playSoundAlive(Sound sound, float pitch) {
        for (MatchTeam team : teams) {
            team.playSoundAlive(sound, pitch);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Match && ((Match) o).id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}