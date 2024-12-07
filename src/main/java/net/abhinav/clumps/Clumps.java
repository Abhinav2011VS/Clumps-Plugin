package net.abhinav.clumps;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Clumps extends JavaPlugin {

    private double mergeRadius;
    private int minXpToMerge;
    private int mergeInterval;
    private double instantCollectRadius;

    @Override
    public void onEnable() {
        // Load configuration and register events
        saveDefaultConfig();
        loadConfigValues();

        // Register event listener for XP absorption
        new XPCollectionListener(this).register();

        // Schedule periodic merging task
        new MergeTask(this).runTaskTimer(this, 0, mergeInterval * 20L);

        getLogger().info("Clumps enabled with fast XP absorption and merging.");
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        mergeRadius = config.getDouble("merge-radius", 2.0);
        minXpToMerge = config.getInt("min-xp-to-merge", 5);
        mergeInterval = config.getInt("merge-interval-seconds", 10);
        instantCollectRadius = config.getDouble("instant-collect-radius", 1.5);
    }

    public double getMergeRadius() {
        return mergeRadius;
    }

    public int getMinXpToMerge() {
        return minXpToMerge;
    }

    public int getMergeInterval() {
        return mergeInterval;
    }

    public double getInstantCollectRadius() {
        return instantCollectRadius;
    }

    @Override
    public void onDisable() {
        getLogger().info("Clumps has been disabled.");
    }
}
