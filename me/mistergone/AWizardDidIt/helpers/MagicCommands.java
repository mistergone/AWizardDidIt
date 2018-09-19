package me.mistergone.AWizardDidIt.helpers;

// This Class exists mostly to debug stuff

import me.mistergone.AWizardDidIt.AWizardDidIt;
import me.mistergone.AWizardDidIt.MagicSpell;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class MagicCommands implements CommandExecutor {

    private final AWizardDidIt plugin;

    public MagicCommands( AWizardDidIt plugin ) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("wizardry")) {
            if (args.length == 0) {
                return false;
            } else if (args.length == 1 ) {
                if (args[0].equalsIgnoreCase("version")) {
                    sender.sendMessage("This server is running version " + plugin.getDescription().getVersion());
                    return true;
                } else if ( args[0].equalsIgnoreCase( "mywizardpower" ) ) {
                    if ( sender instanceof Player ) {
                        Player p = (Player) sender;
                        int wizardPower = getWizardry().getWizardPlayer( p.getUniqueId() ).getWizardPower();
                        getWizardry().getWizardPlayer( p.getUniqueId() ).showWizardBar();
                        p.sendMessage( "You have " + String.valueOf( wizardPower  )
                                +  " points of Wizard Power.");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase( "me" ) ) {
                    if ( sender instanceof Player ) {
                        Player p = (Player) sender;
                        WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( p.getUniqueId() );
                        int wizardPower = wizardPlayer.getWizardPower();
                        getWizardry().getWizardPlayer( p.getUniqueId() ).showWizardBar();
                        p.sendMessage( "You have " + String.valueOf( wizardPower )
                                +  " points of Wizard Power.");
                        if ( wizardPlayer.getSpells().size() > 0 ) {
                            for ( String spell: wizardPlayer.getSpells() ) {
                                p.sendMessage( "You are under the effect of the spell " + ChatColor.GOLD
                                        + spell );
                            }
                        }
                        return true;
                    }
                }
            } else if ( args.length == 2 ) {
                if (args[0].equalsIgnoreCase("getWizardPower") || args[0].equalsIgnoreCase("getwp")) {

                    Player p = this.plugin.getServer().getPlayer(args[1]);
                    if (!sender.isOp()) {
                        sender.sendMessage("This command is for ops only.");
                        return true;
                    }
                    if (p == null) {
                        sender.sendMessage("No such player!");
                        return true;
                    }
                    int wizardPower = getWizardry().getWizardPlayer(p.getUniqueId()).getWizardPower();
                    sender.sendMessage(p.getDisplayName() + " has " + String.valueOf( wizardPower )
                            + " points of Wizard Power.");
                    return true;
                }
            } else if ( args.length == 3 ) {
                if ( args[0].equalsIgnoreCase( "setWizardPower") || args[0].equalsIgnoreCase( "setwp" ) ) {
                    Player p = this.plugin.getServer().getPlayer(args[1]);
                    if (!sender.isOp()) {
                        sender.sendMessage("This command is for ops only.");
                        return true;
                    }
                    if ( isInteger( args[2] ) && p != null ) {
                        WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( p.getUniqueId() );
                        Integer amount = Integer.valueOf( args[2] );
                        wizardPlayer.setWizardPower( amount );
                        int wizardPower = getWizardry().getWizardPlayer(p.getUniqueId()).getWizardPower();
                        sender.sendMessage(p.getDisplayName() + " has " + String.valueOf( wizardPower )
                                + " points of Wizard Power.");
                        return true;
                    } else {
                        if ( p == null ) {
                            sender.sendMessage( "No such player!" );
                            return true;
                        } else {
                            sender.sendMessage( "Third argument is not an Integer." );
                            return true;
                        }
                    }
                }
            } else {
                sender.sendMessage("Invalid argument count.");
                return true;
            }
        }
        return false; // if false is returned, the help for the command stated in the plugin.yml will be displayed to the player
    }

    private Boolean isInteger( String string ) {
        Boolean isInteger = false;
        try {
            Integer.parseInt( string );
            isInteger = true;
        } catch( Exception ex ) {
        }
        return isInteger;
    }
}
