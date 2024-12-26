package net.abhinav.clumps;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MergeTask extends BukkitRunnable {

    private final Clumps plugin;

    public MergeTask(Clumps plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        double mergeRadius = plugin.getMergeRadius();
        int minXpToMerge = plugin.getMinXpToMerge();
        boolean enableMergeAnimations = plugin.isEnableMergeAnimations();
        Set<ExperienceOrb> processedOrbs = new HashSet<>();

        for (World world : Bukkit.getWorlds()) {
            List<ExperienceOrb> orbs = world.getEntitiesByClass(ExperienceOrb.class).stream()
                    .filter(orb -> orb.getExperience() >= minXpToMerge && !orb.isDead())
                    .collect(Collectors.toList());

            for (ExperienceOrb orb : orbs) {
                if (processedOrbs.contains(orb)) continue;

                // Get nearby orbs within merge radius
                List<ExperienceOrb> nearbyOrbs = orb.getNearbyEntities(mergeRadius, mergeRadius, mergeRadius).stream()
                        .filter(entity -> entity instanceof ExperienceOrb)
                        .map(entity -> (ExperienceOrb) entity)
                        .filter(nearbyOrb -> !processedOrbs.contains(nearbyOrb))
                        .collect(Collectors.toList());

                // Merge XP from nearby orbs
                int totalXP = orb.getExperience();
                for (ExperienceOrb nearbyOrb : nearbyOrbs) {
                    totalXP += nearbyOrb.getExperience();
                    nearbyOrb.remove();  // Remove merged orb
                    processedOrbs.add(nearbyOrb);
                }

                orb.setExperience(totalXP);  // Update XP of the current orb

                // Log XP merge
                plugin.getLoggerManager().log("Merged XP from nearby orbs. Total XP: " + totalXP);

                // Add merge animation effect if enabled
                if (enableMergeAnimations) {
                    orb.getWorld().spawnParticle(Particle.END_ROD, orb.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
                }
            }
        }
    }
}
