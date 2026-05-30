package net.abhinav.clumps;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MergeTask implements Runnable {

    private final Clumps plugin;

    public MergeTask(Clumps plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (FoliaUtils.isFolia()) {
            return; // Managed by FoliaUtils scheduling safely per region instead
        }
        
        for (World world : Bukkit.getWorlds()) {
            processOrbs(world.getEntitiesByClass(ExperienceOrb.class));
        }
    }

    public void processChunk(Chunk chunk) {
        List<ExperienceOrb> orbs = new ArrayList<>();
        for (Entity e : chunk.getEntities()) {
            if (e instanceof ExperienceOrb) {
                orbs.add((ExperienceOrb) e);
            }
        }
        processOrbs(orbs);
    }

    private void processOrbs(Collection<ExperienceOrb> orbsToProcess) {
        double mergeRadius = plugin.getMergeRadius();
        double mergeRadiusSq = mergeRadius * mergeRadius;
        int minXpToMerge = plugin.getMinXpToMerge();
        boolean enableMergeAnimations = plugin.isEnableMergeAnimations();
        Set<ExperienceOrb> processedOrbs = new HashSet<>();

        List<ExperienceOrb> orbs = orbsToProcess.stream()
                .filter(orb -> orb.getExperience() >= minXpToMerge && !orb.isDead())
                .collect(Collectors.toList());

        for (ExperienceOrb orb : orbs) {
            if (processedOrbs.contains(orb) || orb.isDead()) continue;

            // Compute distance manually within the provided collection to avoid cross-region entity scans in Folia
            List<ExperienceOrb> nearbyOrbs = orbs.stream()
                    .filter(nearbyOrb -> nearbyOrb != orb && !processedOrbs.contains(nearbyOrb) && !nearbyOrb.isDead())
                    .filter(nearbyOrb -> nearbyOrb.getWorld().equals(orb.getWorld()) && nearbyOrb.getLocation().distanceSquared(orb.getLocation()) <= mergeRadiusSq)
                    .collect(Collectors.toList());

            if (nearbyOrbs.isEmpty()) continue;

            int totalXP = orb.getExperience();
            for (ExperienceOrb nearbyOrb : nearbyOrbs) {
                totalXP += nearbyOrb.getExperience();
                nearbyOrb.remove();
                processedOrbs.add(nearbyOrb);
            }

            orb.setExperience(totalXP);

            if (enableMergeAnimations) {
                orb.getWorld().spawnParticle(Particle.END_ROD, orb.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
            }
        }
    }
}
