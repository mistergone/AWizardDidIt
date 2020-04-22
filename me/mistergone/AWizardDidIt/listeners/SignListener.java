package me.mistergone.AWizardDidIt.listeners;

import me.mistergone.AWizardDidIt.Wizardry;
import me.mistergone.AWizardDidIt.helpers.BlockManager;
import me.mistergone.AWizardDidIt.signs.WizardElevator;
import me.mistergone.AWizardDidIt.signs.UnseenArchitect;
import me.mistergone.AWizardDidIt.signs.WizardPassage;
import me.mistergone.AWizardDidIt.signs.WizardLock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.data.UnseenProjectManager.getUnseenPM;

public class SignListener implements Listener {
    private Wizardry wizardry;

    public SignListener( Wizardry wizardry ) {
        this.wizardry = wizardry;
    }

    @EventHandler
    public void onSignChange( SignChangeEvent event ) {
        String[] lines = event.getLines();
        Block b = event.getBlock();
        if ( lines == null || b == null ) return;

        if ( lines[0].equals("[UnseenArchitect]") ) {
            UnseenArchitect.handleSignEvent( event );
        } else if ( lines[0].equals("[WizardElevator]") ) {
            WizardElevator.handleSignEvent( event );
        } else if ( lines[0].equals( "[WizardPassage]" ) ) {
            WizardPassage.handleSignEvent( event );
        } else if ( lines[0].equals( "[WizardLock]" ) ) {
            WizardLock.handleSignEvent( event );
        }

    }

    @EventHandler(priority= EventPriority.HIGH)
    public void BlockBreakEvent( BlockBreakEvent e ) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        if ( p == null || b == null ) return;

        if ( Tag.SIGNS.isTagged( b.getType() ) ) {
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

            // Prevent owned Wizard sign destruction
            if ( !p.isOp() ) { // Ops can destroy all signs directly
                if ( WizardLock.isWizardLockSign( b ) && !BlockManager.getSignOwner( sign ).equals( p.getName() ) ) {
                    p.sendMessage( ChatColor.RED + "You cannot destroy other player's Wizard Signs!" );
                    e.setCancelled( true );
                }
            }
        }

        // Prevent block destruction if sign is attached.
        ArrayList<Block> signs = BlockManager.getAttachedSigns( b );
        for ( Block blocko: signs ) {
            if ( BlockManager.isWizardSign( blocko ) ) {
                p.sendMessage( ChatColor.RED + "This block has a Wizard Sign attached and cannot be broken! Please " +
                        "break the attached sign first." );
                e.setCancelled( true );
            }
        }
    }

    @EventHandler
    public void BlockPlaceEvent( BlockPlaceEvent e ) {
        Material placedType = e.getBlockPlaced().getType();
        if ( placedType == Material.CHEST || placedType == Material.TRAPPED_CHEST ) {
            Block up = e.getBlockPlaced().getRelative( BlockFace.UP );
            Player p = e.getPlayer();
            if ( WizardLock.isWizardLockSign( up ) ) {
                Sign s = (Sign) up.getState();
                if ( !BlockManager.getSignOwner( s ).equals( p.getName() ) ) {
                    p.sendMessage( ChatColor.RED + "You cannot place a chest under another player's WizardLock!");
                    e.setCancelled( true );
                }
            }
        }
    }

    public static void wipeSign( Location loc ) {
        if ( loc.getBlock() != null && Tag.SIGNS.isTagged( loc.getBlock().getType() ) ) {
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
