package net.abhinav.clumps;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Clumps extends JavaPlugin {

    private double mergeRadius;
    private int minXpToMerge;
    private int mergeInterval;
    private double instantCollectRadius;
    private boolean enableInstantCollect;
    private boolean enableMerging;
    private double xpBoostMultiplier;
    private boolean enableXPBoost;
    private boolean enableMergeAnimations;
    private boolean preventOrbDuplication;

    @Override
    public void onEnable() {
        // Load configuration and register events
        saveDefaultConfig();
        loadConfigValues();

        // Register event listener for XP absorption if enabled
        if (enableInstantCollect) {
            new XPCollectionListener(this).register();
        }

        // Schedule periodic merging task if enabled
        if (enableMerging) {
            new MergeTask(this).runTaskTimer(this, 0, mergeInterval * 20L);
        }

        getLogger().info("Clumps enabled with extended features.");
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        mergeRadius = config.getDouble("merge-radius", 2.0);
        minXpToMerge = config.getInt("min-xp-to-merge", 5);
        mergeInterval = config.getInt("merge-interval-seconds", 10);
        instantCollectRadius = config.getDouble("instant-collect-radius", 1.5);
        enableInstantCollect = config.getBoolean("enable-instant-collect", true);
        enableMerging = config.getBoolean("enable-merging", true);
        xpBoostMultiplier = config.getDouble("xp-boost-multiplier", 1.1); // 10% bonus XP
        enableXPBoost = config.getBoolean("enable-xp-boost", true);
        enableMergeAnimations = config.getBoolean("enable-merge-animations", true);
        preventOrbDuplication = config.getBoolean("prevent-orb-duplication", true);
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

    public double getXpBoostMultiplier() {
        return xpBoostMultiplier;
    }

    public boolean isEnableXPBoost() {
        return enableXPBoost;
    }

    public boolean isEnableMergeAnimations() {
        return enableMergeAnimations;
    }

    public boolean isPreventOrbDuplication() {
        return preventOrbDuplication;
    }

    @Override
    public void onDisable() {
        getLogger().info("Clumps has been disabled.");
    }
}
