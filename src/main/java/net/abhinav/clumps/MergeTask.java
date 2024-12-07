package net.abhinav.clumps;

import org.bukkit.Bukkit;
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

        for (World world : Bukkit.getWorlds()) {
            // Fetch experience orbs directly, avoiding excessive entity checks
            List<ExperienceOrb> orbs = (List<ExperienceOrb>) world.getEntitiesByClass(ExperienceOrb.class);

            Set<ExperienceOrb> processedOrbs = new HashSet<>();
            for (ExperienceOrb orb : orbs) {
                if (orb.isDead() || orb.getExperience() < minXpToMerge || processedOrbs.contains(orb)) continue;

                List<ExperienceOrb> nearbyOrbs = orb.getNearbyEntities(mergeRadius, mergeRadius, mergeRadius)
                        .stream()
                        .filter(e -> e instanceof ExperienceOrb)
                        .map(e -> (ExperienceOrb) e)
                        .collect(Collectors.toList());

                int totalXP = orb.getExperience();
                for (ExperienceOrb nearbyOrb : nearbyOrbs) {
                    if (processedOrbs.contains(nearbyOrb)) continue;  // Skip already processed orbs
                    totalXP += nearbyOrb.getExperience();
                    nearbyOrb.remove();  // Mark orb for removal after processing
                    processedOrbs.add(nearbyOrb);  // Mark orb as processed
                }

                orb.setExperience(totalXP);  // Update the orb with the merged XP
            }
        }
    }
}
