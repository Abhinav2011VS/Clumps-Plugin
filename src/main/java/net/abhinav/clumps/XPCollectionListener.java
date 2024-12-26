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

        // Only collect XP if radius is set to something greater than zero
        if (instantCollectRadius <= 0) return;

        // Find nearby orbs and collect their XP
        List<ExperienceOrb> nearbyOrbs = world.getNearbyEntities(playerLocation, instantCollectRadius, instantCollectRadius, instantCollectRadius)
                .stream()
                .filter(entity -> entity instanceof ExperienceOrb)
                .map(entity -> (ExperienceOrb) entity)
                .collect(Collectors.toList());

        for (ExperienceOrb orb : nearbyOrbs) {
            player.giveExp(orb.getExperience());  // Give player XP from the orb
            orb.remove();  // Remove orb after collection

            // Log XP collection
            plugin.getLoggerManager().log(player.getName() + " collected " + orb.getExperience() + " XP from nearby orb.");
        }
    }
}
