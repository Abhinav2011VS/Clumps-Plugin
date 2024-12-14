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

        Set<ExperienceOrb> processedOrbs = new HashSet<>();  // Track processed orbs to avoid multiple processing

        for (World world : Bukkit.getWorlds()) {
            // Get all XP orbs that are not dead and above the minimum XP
            List<ExperienceOrb> orbs = world.getEntitiesByClass(ExperienceOrb.class)
                    .stream()
                    .filter(orb -> orb.getExperience() >= minXpToMerge && !orb.isDead())
                    .collect(Collectors.toList());

            for (ExperienceOrb orb : orbs) {
                if (processedOrbs.contains(orb)) continue;

                // Find nearby orbs within the merge radius
                List<ExperienceOrb> nearbyOrbs = orb.getNearbyEntities(mergeRadius, mergeRadius, mergeRadius)
                        .stream()
                        .filter(entity -> entity instanceof ExperienceOrb)
                        .map(entity -> (ExperienceOrb) entity)
                        .filter(nearbyOrb -> !processedOrbs.contains(nearbyOrb))
                        .collect(Collectors.toList());

                int totalXP = orb.getExperience();
                for (ExperienceOrb nearbyOrb : nearbyOrbs) {
                    totalXP += nearbyOrb.getExperience();
                    nearbyOrb.remove();  // Remove merged orb
                    processedOrbs.add(nearbyOrb);
                }

                orb.setExperience(totalXP);  // Update XP of the current orb

                // Optionally apply an animation effect when merging
                if (enableMergeAnimations) {
                    orb.getWorld().spawnParticle(Particle.END_ROD, orb.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
                }

                // Optionally boost the XP value for nearby players
                if (plugin.isEnableXPBoost()) {
                    orb.getWorld().getNearbyEntities(orb.getLocation(), mergeRadius, mergeRadius, mergeRadius)
                            .stream()
                            .filter(entity -> entity instanceof org.bukkit.entity.Player)
                            .forEach(entity -> {
                                org.bukkit.entity.Player player = (org.bukkit.entity.Player) entity;
                                player.giveExp((int) (orb.getExperience() * plugin.getXpBoostMultiplier())); // Apply XP Boost
                            });
                }
            }
        }
    }
}
