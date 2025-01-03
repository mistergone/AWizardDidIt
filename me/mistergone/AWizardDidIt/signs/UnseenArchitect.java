package me.mistergone.AWizardDidIt.signs;

import me.mistergone.AWizardDidIt.listeners.SignListener;
import me.mistergone.AWizardDidIt.baseClasses.MagicSign;
import me.mistergone.AWizardDidIt.helpers.BlockHelper;
import me.mistergone.AWizardDidIt.baseClasses.SignFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;
import static me.mistergone.AWizardDidIt.data.UnseenProjectManager.getUnseenPM;

public class UnseenArchitect extends MagicSign {

    private static String signTitle = ChatColor.DARK_RED + "[UnseenArchitect]";
    private static final List directions = Arrays.asList( new String[] {
            "NE", "NW", "SE", "SW"
    } );
    private static List validCommands = Arrays.asList( new String[] {
            "point1", "point2",
            "clone", "clone ne", "clone se", "clone sw", "clone nw"
    } );

    public UnseenArchitect() {
        signName = "Unseen Architect";
        signature = "[UnseenArchitect]";

        signFunction = new SignFunction() {
            @Override
            public void run() {
                Sign sign = (Sign) signBlock.getState();
                String[] lines = sign.getSide(Side.FRONT).getLines();

                String projectName = ChatColor.stripColor( lines[1] ).toLowerCase().trim();
                String command = ChatColor.stripColor(lines[2]).trim();
                Boolean exists = lines[1] != null && getUnseenPM().checkProjectExists( player, projectName );
                Boolean isClone = command.substring( 0, 5 ).equalsIgnoreCase( "clone" );
                Boolean isPoint1 = command.equals( "point1" );
                Boolean isPoint2 = command.equals( "point2" );

                if ( exists ) {
                    WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                    if ( wizardPlayer.getUnseenAssistant().getIsWorking() != null ) {
                        player.sendMessage( ChatColor.RED + signName + " could not be invoked! " + wizardPlayer.getUnseenAssistant().getIsWorking() );
                        return;
                    }
                    String projectKey = player.getName() + ":" + projectName;
                    if ( isClone ) {
                        wizardPlayer.getUnseenAssistant().architectClone( projectKey, signBlock, command );
                    } else if ( isPoint1 || isPoint2 ) {
                        Location point1 = getUnseenPM().getProjectPoints( player, projectName )[0];
                        Location point2 = getUnseenPM().getProjectPoints( player, projectName )[1];
                        if ( isPoint1 && point1 != null && point1.equals( signBlock.getLocation() ) ) {
                            player.sendMessage( ChatColor.YELLOW + "Your Unseen Assistant has verified that this location is stored as Point 1 for the Unseen Project " + projectName );
                        } else if ( isPoint2 && point2 != null && point2.equals( signBlock.getLocation() ) ) {
                            player.sendMessage( ChatColor.YELLOW + "Your Unseen Assistant has verified that this location is stored as Point 2 for the Unseen Project " + projectName );
                        } else if ( isPoint1 ) {
                            getUnseenPM().setProjectPoint( player, projectName, 0, signBlock.getLocation() );
                            player.sendMessage( ChatColor.YELLOW + "Your Unseen Assistant has updated Point 1 of the Unseen Project \""
                                    + projectName + "\" to this location.");
                        } else if ( isPoint2 ) {
                            getUnseenPM().setProjectPoint( player, projectName, 1, signBlock.getLocation() );
                            player.sendMessage( ChatColor.YELLOW + "Your Unseen Assistant has updated Point 2 of the Unseen Project \""
                                    + projectName + "\" to this location.");
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "The Unseen Project on this sign, " + projectName + " could not be found!");
                    if ( isPoint1 || isPoint2 ) {
                        player.sendMessage(ChatColor.YELLOW + "Your Unseen Assistant is attempting to fix this...");
                        SignChangeEvent event = new SignChangeEvent( signBlock, player, lines );
                        handleSignEvent( event );
                    }
                }
            }

        };
    }

    public static void handleSignEvent(SignChangeEvent event) {
        String[] lines = event.getLines();
        Player p = event.getPlayer();
        Pattern alphanum = Pattern.compile("[^a-zA-Z0-9]");
        // Check the project name
        String projectName = ChatColor.stripColor( lines[1] );
        if ( projectName != null && !projectName.equals("") && alphanum.matcher( projectName ).find()) {
            p.sendMessage(ChatColor.RED + "The sign does not contain a valid project name! (" + projectName + ")" );
            for (int l = 0; l < 4; l++) {
                lines[l] = "???";
            }
            return;
        }
        // Check the command
        String command = ChatColor.stripColor( lines[2] ).toLowerCase();
        if ( command == null || !validCommands.contains( command ) ) {
            p.sendMessage( ChatColor.RED + "No valid command was found on this sign!");
            for (int l = 0; l < 4; l++) {
                lines[l] = "???";
            }
            return;
        }

        // lines[1] is project name. Does it exist? Get the Location
        if (getUnseenPM().checkProjectExists( p, projectName ) ) {
            p.sendMessage( ChatColor.AQUA + "Your project, \"" + projectName + "\" was found. Modifying project...");
        } else {
            p.sendMessage("Adding a new project...");
            getUnseenPM().addUnseenProject( p, projectName );
        }
        Location[] points = getUnseenPM().getProjectPoints( projectName );
        Location newLoc = event.getBlock().getLocation();

        // lines[2] is the command. Check out commands.
        if ( command.equalsIgnoreCase("point1") || command.equalsIgnoreCase("point2")) {
            int index = 0;
            int otherPoint = 1;
            if ( lines[2].equalsIgnoreCase("point2" ) ) {
                index = 1;
                otherPoint = 0;
            }
            if ( points != null && points[index] != null ) {
                p.sendMessage(ChatColor.LIGHT_PURPLE + "This point already exists at (" + BlockHelper.locToString(points[index])
                        + "). The sign there will be wiped!");
                SignListener.wipeSign(points[index]);
            }
            if ( points != null && points[otherPoint] != null ) {
                // Check max distance between signs
                double dist = points[otherPoint].distance( newLoc );
                if ( dist > 72 ) {
                    p.sendMessage( ChatColor.RED + "This point is too far from the other project point!" );
                    p.sendMessage( ChatColor.RED + "The maximum distance is 72. This distance is " + String.valueOf( (int)Math.ceil( dist ) ) + "." );
                    for (int l = 0; l < 4; l++) {
                        lines[l] = "???";
                    }
                    return;
                }

            }
            getUnseenPM().setProjectPoint( p, projectName, index, newLoc );
            p.sendMessage(ChatColor.AQUA + "Unseen Project: " + projectName + " point #" + String.valueOf(index + 1) + " updated!");
            lines[2] = ChatColor.DARK_BLUE + "point" + String.valueOf(index + 1);
        } else if ( command.substring( 0, 5 ).equalsIgnoreCase("clone") ) {
            if ( command.length() == 5 ) {
                command = command + " SE";
            } else if ( !directions.contains( command.substring( 6 ).toUpperCase() ) ) {
                p.sendMessage(ChatColor.RED + "Unseen Architect command \"" + command + "\" not recognized!");
                for (int l = 0; l < 4; l++) {
                    lines[l] = "???";
                }
                return;
            }
            command = "clone " + command.substring( 6 ).toUpperCase();
            p.sendMessage(ChatColor.BLUE + "To clone the original use the Unseen Architect Magic Pattern in a chest adjacent to this sign. Make sure it's not inside the new structure!");
            lines[2] = ChatColor.DARK_BLUE + command;
        } else {
            p.sendMessage(ChatColor.RED + "Unseen Architect command \"" + lines[2] + "\" not recognized!");
            for (int l = 0; l < 4; l++) {
                lines[l] = "???";
            }
            return;
        }
        lines[1] = ChatColor.DARK_BLUE + projectName;
        lines[0] = signTitle;
        lines[3] = ChatColor.LIGHT_PURPLE + p.getName();

    }

}
