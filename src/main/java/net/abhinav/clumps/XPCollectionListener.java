package net.abhinav.clumps;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import java.util.List;
import java.util.stream.Collectors;

public class XPCollectionListener implements Listener {

    private final Clumps plugin;

    public XPCollectionListener(Clumps plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerNearbyXPCollect(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Location playerLocation = player.getLocation();

        double instantCollectRadius = plugin.getInstantCollectRadius();

        // Optimize entity lookup by filtering early
        List<ExperienceOrb> nearbyOrbs = world.getNearbyEntities(playerLocation, instantCollectRadius, instantCollectRadius, instantCollectRadius)
                .stream()
                .filter(e -> e instanceof ExperienceOrb)
                .map(e -> (ExperienceOrb) e)
                .collect(Collectors.toList());

        // Avoid creating new lists and reduce overhead
        for (ExperienceOrb orb : nearbyOrbs) {
            player.giveExp(orb.getExperience()); // Instantly add XP to the player
            orb.remove(); // Remove the orb to prevent it from floating
        }
    }
}
