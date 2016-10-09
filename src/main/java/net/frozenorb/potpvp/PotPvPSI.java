package net.frozenorb.potpvp;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import net.frozenorb.potpvp.setting.SettingHandler;

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

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        setupMongo();
        setupRedis();

        settingHandler = new SettingHandler();
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