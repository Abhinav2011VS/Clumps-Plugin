package net.abhinav.clumps;

import net.abhinav.clumps.Clumps;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandsTabCompleter implements TabCompleter {

    private final Clumps plugin;

    public CommandsTabCompleter(Clumps plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Add the possible options to the list (e.g., set-merge-radius, toggle-merging, showconfig, etc.)
            completions.addAll(Arrays.asList(
                "set-merge-radius", "set-min-xp-to-merge", "set-merge-interval",
                "set-instant-collect-radius", "toggle-instant-collect",
                "toggle-merging", "set-xp-boost-multiplier",
                "toggle-xp-boost", "toggle-merge-animations",
                "toggle-orb-duplication", "showconfig"
            ));
        } else if (args.length == 2) {
            // Depending on the first argument, provide value suggestions.
            String option = args[0].toLowerCase();
            switch (option) {
                case "set-merge-radius":
                case "set-instant-collect-radius":
                case "set-xp-boost-multiplier":
                    completions.add("<number>");
                    break;
                case "set-min-xp-to-merge":
                case "set-merge-interval":
                    completions.add("<integer>");
                    break;
                case "toggle-instant-collect":
                case "toggle-merging":
                case "toggle-xp-boost":
                case "toggle-merge-animations":
                case "toggle-orb-duplication":
                    completions.addAll(Arrays.asList("true", "false"));
                    break;
            }
        }

        return completions;
    }
}
