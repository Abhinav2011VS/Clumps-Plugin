package net.abhinav.clumps;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FoliaUtils {
    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void scheduleMergeTask(Clumps plugin, MergeTask task, int intervalSeconds) {
        if (isFolia()) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.getScheduler().run(plugin, t -> task.processPlayerOrbs(player), null);
                }
            }, 1L, intervalSeconds * 20L);
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, task, 0L, intervalSeconds * 20L);
        }
    }
}
