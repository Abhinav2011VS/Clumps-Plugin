package net.abhinav.clumps;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class Clumps extends JavaPlugin implements Listener {

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
        Bukkit.getPluginManager().registerEvents(this, this);

        // Schedule periodic merging task
        new MergeTask().runTaskTimer(this, 0, mergeInterval * 20L);

        getLogger().info("ClumpsPlugin enabled with fast XP absorption and merging.");
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        mergeRadius = config.getDouble("merge-radius", 2.0);
        minXpToMerge = config.getInt("min-xp-to-merge", 5);
        mergeInterval = config.getInt("merge-interval-seconds", 10);
        instantCollectRadius = config.getDouble("instant-collect-radius", 1.5);
    }

    // Periodic merging task
    private class MergeTask extends BukkitRunnable {
        @Override
        public void run() {
            for (World world : Bukkit.getWorlds()) {
                List<ExperienceOrb> orbs = (List<ExperienceOrb>) world.getEntitiesByClass(ExperienceOrb.class);

                for (ExperienceOrb orb : orbs) {
                    if (orb.isDead() || orb.getExperience() < minXpToMerge) continue;

                    List<ExperienceOrb> nearbyOrbs = orb.getNearbyEntities(mergeRadius, mergeRadius, mergeRadius)
                            .stream()
                            .filter(e -> e instanceof ExperienceOrb)
                            .map(e -> (ExperienceOrb) e)
                            .collect(Collectors.toList());

                    int totalXP = orb.getExperience();
                    for (ExperienceOrb nearbyOrb : nearbyOrbs) {
                        totalXP += nearbyOrb.getExperience();
                        nearbyOrb.remove();
                    }

                    orb.setExperience(totalXP);
                }
            }
        }
    }

    // Event handler for instant XP collection
    @EventHandler
    public void onPlayerNearbyXPCollect(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Location playerLocation = player.getLocation();

        // Find XP orbs within the instant collect radius
        List<ExperienceOrb> nearbyOrbs = world.getNearbyEntities(playerLocation, instantCollectRadius, instantCollectRadius, instantCollectRadius)
                .stream()
                .filter(e -> e instanceof ExperienceOrb)
                .map(e -> (ExperienceOrb) e)
                .collect(Collectors.toList());

        // Absorb XP from each nearby orb
        for (ExperienceOrb orb : nearbyOrbs) {
            player.giveExp(orb.getExperience()); // Instantly add XP to the player
            orb.remove(); // Remove the orb to prevent it from floating
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("ClumpsPlugin has been disabled.");
    }
}
