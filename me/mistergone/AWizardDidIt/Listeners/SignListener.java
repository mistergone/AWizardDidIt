package me.mistergone.AWizardDidIt.Listeners;

import me.mistergone.AWizardDidIt.ToolPattern;
import me.mistergone.AWizardDidIt.Wizardry;
import me.mistergone.AWizardDidIt.data.UnseenProjectManager;
import me.mistergone.AWizardDidIt.helpers.ToolFunction;
import me.mistergone.AWizardDidIt.patterns.UnseenArchitect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitTask;

import static me.mistergone.AWizardDidIt.data.UnseenProjectManager.getUnseenPM;

import java.util.List;

public class SignListener implements Listener {
    private Wizardry wizardry;

    public SignListener( Wizardry wizardry ) {
        this.wizardry = wizardry;
    }

    @EventHandler
    public void onSignChange( SignChangeEvent event ) {
        String[] lines = event.getLines();
        if ( event.getBlock().getType() == Material.SIGN && lines != null && lines[0].equals("[UnseenArchitect]") ) {
            UnseenArchitect.handleSignEvent( event );
        }

    }

    @EventHandler(priority= EventPriority.HIGH)
    public void BlockBreakEvent( BlockBreakEvent e ) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        if ( p != null ) {
            if ( b.getType() == Material.SIGN ) {
                Sign sign = (Sign) b.getState();
                String[] lines = sign.getLines();
                // Handle Unseen Assistant signs
                if ( getUnseenPM().isUASign( lines[0] ) ) {
                    if ( lines[3] != null && lines[3] != p.getName() && !p.isOp() ) {
                        p.sendMessage(ChatColor.DARK_PURPLE + "You cannot break another player's Unseen Assistant marker! ");
                        e.setCancelled(true);
                        return;
                    }
                    String projectName = ChatColor.stripColor( lines[1] ).toLowerCase();
                    if ( lines[1] != null && getUnseenPM().checkProjectExists( p, projectName ) ) {
                        Location[] points = getUnseenPM().getProjectPoints( p, projectName );
                        String command = ChatColor.stripColor( lines[2] );
                        if ( command.equals( "point1" ) ) {
                            points[0] = null;
                            getUnseenPM().setProjectPoint( p, projectName, 0, null );
                        } else if ( command.equals( "point2" ) ) {
                            getUnseenPM().setProjectPoint( p, projectName, 1, null );
                            points[1] = null;
                        }
                        if ( points[0] == null && points[1] == null ) {
                            p.sendMessage( ChatColor.LIGHT_PURPLE + "Your project, \"" + projectName + "\", has no valid points and has been deleted." );
                            getUnseenPM().removeUnseenProject( p, projectName );
                        }
                    } else if ( projectName == null ) {
                        System.out.println( "<AWDI>: A null project name was found on a sign.");
                    } else {
                        System.out.println( "<AWDI>: A sign was destroyed that referred to a non-existent project.");
                    }
                }

            }
        }
    }

    public static void wipeSign( Location loc ) {
        if ( loc.getBlock() != null && loc.getBlock().getType() == Material.SIGN ) {
            Sign sign = (Sign)loc.getBlock().getState();
            for (int i = 0; i < 4; i++ ) {
                sign.setLine( i, "???");
            }
            sign.update( true );
        } else {
            System.out.println( "<AWDI>: wipeSign did not find a sign" );
        }
    }

}
