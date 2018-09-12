package me.mistergone.AWizardDidIt.helpers;

// This Class exists mostly to debug stuff

import me.mistergone.AWizardDidIt.AWizardDidIt;
import me.mistergone.AWizardDidIt.MagicSpell;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MagicCommands implements CommandExecutor {

    private final AWizardDidIt plugin;

    public MagicCommands( AWizardDidIt plugin ) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("wizardry")) {
            if (args.length == 0) {
                return false;
            }
            if (args.length==1) {
                if (args[0].equalsIgnoreCase("version")) {
                    sender.sendMessage("This server is running version " + plugin.getDescription().getVersion());
                    // do other stuff here directly or call some methods
                    return true;
                } //END version
            } else {
                sender.sendMessage("Invalid argument count.");
            }
        }
        return false; // if false is returned, the help for the command stated in the plugin.yml will be displayed to the player
    }
}
