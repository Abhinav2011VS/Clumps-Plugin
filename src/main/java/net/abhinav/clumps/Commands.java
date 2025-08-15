package net.abhinav.clumps;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class Commands implements CommandExecutor {

    private final Clumps plugin;

    public Commands(Clumps plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("clumps.admin")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to modify the configuration.");
            return false;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /clumps <command>");
            return false;
        }

        String option = args[0].toLowerCase();

        switch (option) {
            case "showconfig":
                showConfig(player);
                break;

            case "set-merge-radius":
                if (args.length < 2) return false;
                try {
                    double oldMergeRadius = plugin.getMergeRadius(); 
                    double newMergeRadius = Double.parseDouble(args[1]);
                    plugin.getConfig().set("merge-radius", newMergeRadius);
                    plugin.saveConfig();
                    player.sendMessage(ChatColor.GREEN + "Merge radius changed from " + ChatColor.WHITE + oldMergeRadius + " " + ChatColor.GREEN + "to " + ChatColor.WHITE + newMergeRadius);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid value for merge radius.");
                }
                break;

            // Implement similar changes for other commands...

            default:
                player.sendMessage(ChatColor.RED + "Unknown option: " + option);
                break;
        }

        plugin.reloadConfig();
        player.sendMessage(ChatColor.YELLOW + "Config has been reloaded.");
        return true;
    }

    private void showConfig(Player player) {
        player.sendMessage(ChatColor.GOLD + "Current Configuration:");
        player.sendMessage(ChatColor.GRAY + "Merge Radius: " + ChatColor.WHITE + plugin.getMergeRadius());
        player.sendMessage(ChatColor.GRAY + "Min XP to Merge: " + ChatColor.WHITE + plugin.getMinXpToMerge());
        player.sendMessage(ChatColor.GRAY + "Merge Interval: " + ChatColor.WHITE + plugin.getMergeInterval() + " seconds");
        player.sendMessage(ChatColor.GRAY + "Instant Collect Radius: " + ChatColor.WHITE + plugin.getInstantCollectRadius());
        player.sendMessage(ChatColor.GRAY + "XP Boost Multiplier: " + ChatColor.WHITE + plugin.getXpBoostMultiplier());
    }
}
