package net.abhinav.clumps;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

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
                Set<String> processedChunks = new HashSet<>();
                int r = 4; // 4 chunks radius roughly matches our old 64 blocks radius

                for (Player player : Bukkit.getOnlinePlayers()) {
                    Location loc = player.getLocation();
                    World world = loc.getWorld();
                    int cx = loc.getBlockX() >> 4;
                    int cz = loc.getBlockZ() >> 4;
                    
                    for (int x = cx - r; x <= cx + r; x++) {
                        for (int z = cz - r; z <= cz + r; z++) {
                            String key = world.getUID() + ":" + x + ":" + z;
                            if (processedChunks.add(key)) {
                                final int chunkX = x;
                                final int chunkZ = z;
                                Bukkit.getRegionScheduler().run(plugin, world, chunkX, chunkZ, t -> {
                                    if (world.isChunkLoaded(chunkX, chunkZ)) {
                                        task.processChunk(world.getChunkAt(chunkX, chunkZ));
                                    }
                                });
                            }
                        }
                    }
                }
            }, 1L, intervalSeconds * 20L);
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, task, 0L, intervalSeconds * 20L);
        }
    }
}
