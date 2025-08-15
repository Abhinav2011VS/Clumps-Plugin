package net.abhinav.clumps.listeners;

import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class XPCollectionListener implements Listener {

    private final Clumps plugin;

    public XPCollectionListener(Clumps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerNearbyXPCollect(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        double radius = plugin.getInstantCollectRadius();

        // Skip if no collection radius is set
        if (radius <= 0) return;

        List<ExperienceOrb> orbs = player.getWorld().getEntitiesByClass(ExperienceOrb.class)
                .stream()
                .filter(orb -> orb.getLocation().distance(player.getLocation()) <= radius)
                .collect(Collectors.toList());

        for (ExperienceOrb orb : orbs) {
            player.giveExp(orb.getExperience());
            orb.remove();
            player.sendMessage(ChatColor.GREEN + "XP collected from an orb nearby!");
        }
    }
}
