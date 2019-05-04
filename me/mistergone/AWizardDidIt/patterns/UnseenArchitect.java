package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.Listeners.SignListener;
import me.mistergone.AWizardDidIt.MagicPattern;
import me.mistergone.AWizardDidIt.helpers.BlockManager;
import me.mistergone.AWizardDidIt.helpers.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;
import static me.mistergone.AWizardDidIt.data.UnseenProjectManager.getUnseenPM;


public class UnseenArchitect extends MagicPattern {
    private static String signTitle = ChatColor.DARK_RED + "[UnseenArchitect]";
    public static ArrayList<String> directions = new ArrayList<>();

    public UnseenArchitect() {
        patternName = "Unseen Architect";
        patterns = new ArrayList<String[]>();
        patterns.add(new String[]
                {"GLOWSTONE_DUST", "SOUL_SAND", "GLOWSTONE_DUST",
                        "SOUL_SAND", "GLISTERING_MELON_SLICE", "SOUL_SAND",
                        "GLOWSTONE_DUST", "SOUL_SAND", "GLOWSTONE_DUST"});

        directions.add( "NE" );
        directions.add( "NW" );
        directions.add( "SE" );
        directions.add( "SW" );

        patternFunction = new PatternFunction() {
            @Override
            public void run() {
                Block up = magicChest.getChest().getBlock().getRelative(BlockFace.UP);
                if ( Tag.SIGNS.isTagged(up.getType() ) ) {
                    Sign sign = (Sign) up.getState();
                    String[] lines = sign.getLines();
                    Boolean isUA = lines[0].equals(signTitle);
                    String projectName = ChatColor.stripColor( lines[1] ).toLowerCase();
                    String command = ChatColor.stripColor(lines[2]);
                    Boolean exists = lines[1] != null && getUnseenPM().checkProjectExists( player, projectName );
                    Boolean isClone = command.substring( 0, 5 ).equalsIgnoreCase("clone");
                    if ( isUA && exists ) {
                        WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                        if ( wizardPlayer.getUnseenAssistant().getIsWorking() != null ) {
                            player.sendMessage( ChatColor.RED + patternName + " could not be invoked! " + wizardPlayer.getUnseenAssistant().getIsWorking() );
                        } else {
                            if ( isClone ) {
                                String projectKey = player.getName() + ":" + projectName;
                                wizardPlayer.getUnseenAssistant().architectClone( projectKey, up, magicChest.getChest(), command );
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "The Unseen Project on this sign could not be found!");
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
        String projectName = lines[1];
        if ( projectName != null && !alphanum.matcher( projectName ).find()) {
            // lines[1] is project name. Check it out.
            if (getUnseenPM().checkProjectExists( p, projectName ) ) {
                p.sendMessage( ChatColor.AQUA + "Your project, \"" + projectName + "\" was found. Modifying project...");
            } else {
                p.sendMessage("Adding a new project...");
                getUnseenPM().addUnseenProject(p, projectName);
            }
            // lines[2] is command. Check it out.
            Location[] points = getUnseenPM().getProjectPoints( projectName );
            Location newLoc = event.getBlock().getLocation();
            String command = lines[2];
            if ( command.equalsIgnoreCase("point1") || command.equalsIgnoreCase("point2")) {
                int index = 0;
                int otherPoint = 1;
                if ( lines[2].equalsIgnoreCase("point2" ) ) {
                    index = 1;
                    otherPoint = 0;
                }
                if ( points != null && points[index] != null ) {
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "This point already exists at (" + BlockManager.locToString(points[index])
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
                } else if ( !directions.contains( command.substring( 6 ) ) ) {
                    p.sendMessage(ChatColor.RED + "Unseen Architect command \"" + command + "\" not recognized!");
                    for (int l = 0; l < 4; l++) {
                        lines[l] = "???";
                    }
                    return;
                }
                p.sendMessage(ChatColor.BLUE + "To clone the original use the Unseen Architect Magic Pattern in a chest under this sign.");
                lines[2] = ChatColor.DARK_BLUE + command;
            } else {
                p.sendMessage(ChatColor.RED + "Unseen Architect command \"" + lines[2] + "\" not recognized!");
                for (int l = 0; l < 4; l++) {
                    lines[l] = "???";
                }
                return;
            }
            lines[1] = ChatColor.DARK_BLUE + projectName;
        } else {
            p.sendMessage(ChatColor.RED + "The sign does not contain a valid project name!");
            for (int l = 0; l < 4; l++) {
                lines[l] = "???";
            }
            return;
        }
        lines[0] = signTitle;
        lines[3] = ChatColor.DARK_PURPLE + p.getName();

    }


}
