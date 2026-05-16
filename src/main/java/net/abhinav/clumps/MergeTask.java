package net.abhinav.clumps;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

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
            return; // We shouldn't call this directly unconditionally on Folia
        }
        
        for (World world : Bukkit.getWorlds()) {
            processOrbs(world.getEntitiesByClass(ExperienceOrb.class));
        }
    }

    public void processPlayerOrbs(Player player) {
        processOrbs(player.getNearbyEntities(64, 64, 64).stream()
                .filter(e -> e instanceof ExperienceOrb)
                .map(e -> (ExperienceOrb) e)
                .collect(Collectors.toList()));
    }

    private void processOrbs(Collection<ExperienceOrb> orbsToProcess) {
        double mergeRadius = plugin.getMergeRadius();
        int minXpToMerge = plugin.getMinXpToMerge();
        boolean enableMergeAnimations = plugin.isEnableMergeAnimations();
        Set<ExperienceOrb> processedOrbs = new HashSet<>();

        List<ExperienceOrb> orbs = orbsToProcess.stream()
                .filter(orb -> orb.getExperience() >= minXpToMerge && !orb.isDead())
                .collect(Collectors.toList());

        for (ExperienceOrb orb : orbs) {
            if (processedOrbs.contains(orb) || orb.isDead()) continue;

            List<ExperienceOrb> nearbyOrbs = orb.getNearbyEntities(mergeRadius, mergeRadius, mergeRadius).stream()
                    .filter(entity -> entity instanceof ExperienceOrb)
                    .map(entity -> (ExperienceOrb) entity)
                    .filter(nearbyOrb -> !processedOrbs.contains(nearbyOrb) && !nearbyOrb.isDead())
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
