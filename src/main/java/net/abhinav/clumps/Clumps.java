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

    private LoggerManager loggerManager;  // Add logger manager

    @Override
    public void onEnable() {
        // Initialize the logger with the plugin instance
        loggerManager = new LoggerManager(this); // Pass 'this' to the LoggerManager

        // Log the plugin startup
        loggerManager.log("Clumps plugin enabled.");

        // Load configuration and register events
        saveDefaultConfig();
        loadConfigValues();

        // Register the command and tab completer
        getCommand("clumps").setExecutor(new Commands(this));
        getCommand("clumps").setTabCompleter(new CommandsTabCompleter(this));

        // Register event listener for XP absorption if enabled
        if (enableInstantCollect) {
            new XPCollectionListener(this).register();
            loggerManager.log("XP instant collect enabled.");
        }

        // Schedule periodic merging task if enabled
        if (enableMerging) {
            new MergeTask(this).runTaskTimer(this, 0, mergeInterval * 20L);
            loggerManager.log("XP merging task started.");
        }

        loggerManager.log("Clumps plugin started with the current configuration.");
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

        // Log config values
        loggerManager.log("Loaded config values: mergeRadius=" + mergeRadius + ", minXpToMerge=" + minXpToMerge +
                ", mergeInterval=" + mergeInterval + ", enableInstantCollect=" + enableInstantCollect +
                ", enableMerging=" + enableMerging + ", xpBoostMultiplier=" + xpBoostMultiplier +
                ", enableXPBoost=" + enableXPBoost + ", enableMergeAnimations=" + enableMergeAnimations +
                ", preventOrbDuplication=" + preventOrbDuplication);
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
        // Log plugin disable
        loggerManager.log("Clumps plugin has been disabled.");

        if (loggerManager != null) {
            loggerManager.close();  // Close the log writer
        }
    }

    // Provide access to the logger for other classes
    public LoggerManager getLoggerManager() {
        return loggerManager;
    }
}
