package net.frozenorb.potpvp;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.matchlog.MatchLogHandler;
import net.frozenorb.potpvp.setting.SettingHandler;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class PotPvPSI extends JavaPlugin {

    @Getter private static PotPvPSI instance;

    private MongoClient mongoClient;
    @Getter private MongoDatabase mongoDatabase;

    @Getter private JedisPool redisConnection;

    @Getter private SettingHandler settingHandler;
    @Getter private ArenaHandler arenaHandler;
    @Getter private MatchHandler matchHandler;
    @Getter private MatchLogHandler matchLogHandler;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        setupMongo();
        setupRedis();

        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setTime(12_000L);
        }

        settingHandler = new SettingHandler();
        arenaHandler = new ArenaHandler();
        matchHandler = new MatchHandler();
        matchLogHandler = new MatchLogHandler();
    }

    @Override
    public void onDisable() {
        instance = null;

        mongoClient.close();
        redisConnection.close();
    }

    private void setupMongo() {
        mongoClient = new MongoClient(
            getConfig().getString("Mongo.Host"),
            getConfig().getInt("Mongo.Port")
        );

        String databaseId = getConfig().getString("Mongo.Database");
        mongoDatabase = mongoClient.getDatabase(databaseId);
    }

    private void setupRedis() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        // Don't block any thread when pool is exhausted
        jedisPoolConfig.setBlockWhenExhausted(false);

        redisConnection = new JedisPool(
            jedisPoolConfig,
            getConfig().getString("Redis.Host"),
            getConfig().getInt("Redis.Port")
        );
    }

}