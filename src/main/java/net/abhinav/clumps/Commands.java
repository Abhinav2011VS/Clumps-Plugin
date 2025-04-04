package net.abhinav.clumps;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    private final Clumps plugin;

    public Commands(Clumps plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players.");
            return false;
        }

        Player player = (Player) sender;

        // Check if the player has permission to use the command
        if (!player.hasPermission("clumps.admin")) {
            player.sendMessage("You do not have permission to modify the configuration.");
            return false;
        }

        if (args.length < 1) {
            player.sendMessage("Usage: /clumps <command>");
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
                    double mergeRadius = Double.parseDouble(args[1]);
                    plugin.getConfig().set("merge-radius", mergeRadius);
                    plugin.saveConfig();
                    player.sendMessage("Merge radius set to " + mergeRadius);
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid value for merge radius.");
                }
                break;

            case "set-min-xp-to-merge":
                if (args.length < 2) return false;
                try {
                    int minXpToMerge = Integer.parseInt(args[1]);
                    plugin.getConfig().set("min-xp-to-merge", minXpToMerge);
                    plugin.saveConfig();
                    player.sendMessage("Minimum XP to merge set to " + minXpToMerge);
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid value for minimum XP to merge.");
                }
                break;

            case "set-merge-interval":
                if (args.length < 2) return false;
                try {
                    int mergeInterval = Integer.parseInt(args[1]);
                    plugin.getConfig().set("merge-interval-seconds", mergeInterval);
                    plugin.saveConfig();
                    player.sendMessage("Merge interval set to " + mergeInterval + " seconds");
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid value for merge interval.");
                }
                break;

            case "set-instant-collect-radius":
                if (args.length < 2) return false;
                try {
                    double instantCollectRadius = Double.parseDouble(args[1]);
                    plugin.getConfig().set("instant-collect-radius", instantCollectRadius);
                    plugin.saveConfig();
                    player.sendMessage("Instant collect radius set to " + instantCollectRadius);
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid value for instant collect radius.");
                }
                break;

            case "toggle-instant-collect":
                boolean currentInstantCollect = plugin.getConfig().getBoolean("enable-instant-collect");
                plugin.getConfig().set("enable-instant-collect", !currentInstantCollect);
                plugin.saveConfig();
                player.sendMessage("Instant collect has been " + (currentInstantCollect ? "disabled" : "enabled"));
                break;

            case "toggle-merging":
                boolean currentMerging = plugin.getConfig().getBoolean("enable-merging");
                plugin.getConfig().set("enable-merging", !currentMerging);
                plugin.saveConfig();
                player.sendMessage("Merging has been " + (currentMerging ? "disabled" : "enabled"));
                break;

            case "set-xp-boost-multiplier":
                if (args.length < 2) return false;
                try {
                    double xpBoostMultiplier = Double.parseDouble(args[1]);
                    plugin.getConfig().set("xp-boost-multiplier", xpBoostMultiplier);
                    plugin.saveConfig();
                    player.sendMessage("XP Boost multiplier set to " + xpBoostMultiplier);
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid value for XP boost multiplier.");
                }
                break;

            case "toggle-xp-boost":
                boolean currentXPBoost = plugin.getConfig().getBoolean("enable-xp-boost");
                plugin.getConfig().set("enable-xp-boost", !currentXPBoost);
                plugin.saveConfig();
                player.sendMessage("XP Boost has been " + (currentXPBoost ? "disabled" : "enabled"));
                break;

            case "toggle-merge-animations":
                boolean currentMergeAnimations = plugin.getConfig().getBoolean("enable-merge-animations");
                plugin.getConfig().set("enable-merge-animations", !currentMergeAnimations);
                plugin.saveConfig();
                player.sendMessage("Merge animations have been " + (currentMergeAnimations ? "disabled" : "enabled"));
                break;

            case "toggle-orb-duplication":
                boolean currentDuplication = plugin.getConfig().getBoolean("prevent-orb-duplication");
                plugin.getConfig().set("prevent-orb-duplication", !currentDuplication);
                plugin.saveConfig();
                player.sendMessage("Orb duplication prevention has been " + (currentDuplication ? "disabled" : "enabled"));
                break;

            default:
                player.sendMessage("Unknown option: " + option);
                break;
        }

        // Reload the plugin to apply the new config changes
        plugin.reloadConfig();
        player.sendMessage("Config has been reloaded.");
        return true;
    }

    private void showConfig(Player player) {
        // Show current config values
        player.sendMessage("§6Current Configuration:");
        player.sendMessage("§7Merge Radius: §f" + plugin.getMergeRadius());
        player.sendMessage("§7Min XP to Merge: §f" + plugin.getMinXpToMerge());
        player.sendMessage("§7Merge Interval: §f" + plugin.getMergeInterval() + " seconds");
        player.sendMessage("§7Instant Collect Radius: §f" + plugin.getInstantCollectRadius());
        player.sendMessage("§7XP Boost Multiplier: §f" + plugin.getXpBoostMultiplier());
        player.sendMessage("§7XP Boost Enabled: §f" + (plugin.isEnableXPBoost() ? "Enabled" : "Disabled"));
        player.sendMessage("§7Merge Animations Enabled: §f" + (plugin.isEnableMergeAnimations() ? "Enabled" : "Disabled"));
        player.sendMessage("§7Prevent Orb Duplication: §f" + (plugin.isPreventOrbDuplication() ? "Enabled" : "Disabled"));
    }
}
