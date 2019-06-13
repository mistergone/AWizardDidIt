package me.mistergone.AWizardDidIt.signs;

import me.mistergone.AWizardDidIt.AWizardDidIt;
import me.mistergone.AWizardDidIt.MagicSign;
import me.mistergone.AWizardDidIt.helpers.SignFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class WizardElevator extends MagicSign {

    public WizardElevator() {
        signName = "Wizard Elevator";
        signature = "[WizardElevator]";
        cost = 5;

        signFunction = new SignFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                wizardPlayer.addSpell( signName );
                int direction = player.isSneaking() ? -1 : 1;
                Block destination = findDestination( clickedBlock, direction, player );
                if ( destination == null ) return;

                moveIt( player, destination, clickedBlock );

            }
        };
    }

    private void moveIt( Player player, Block destination, Block clickedBlock ) {
        AWizardDidIt plugin = (AWizardDidIt)Bukkit.getServer().getPluginManager().getPlugin("AWizardDidIt");
        final Location newLoc = destination.getLocation();
        Location startingPoint = clickedBlock.getLocation().add( 0.5,-1,0.5 );
        startingPoint.setPitch( player.getLocation().getPitch() );
        startingPoint.setYaw( player.getLocation().getYaw() );
        player.teleport( startingPoint );

        new BukkitRunnable(){
            @Override
            public void run() {

                player.setFallDistance(0f);

                if ( Math.abs( newLoc.getY() - player.getLocation().getY() ) < .7 ) {
                    Location endLoc = destination.getLocation().add(0.5,-1,0.5);
                    endLoc.setYaw( player.getLocation().getYaw() );
                    endLoc.setPitch( player.getLocation().getPitch() );
                    player.teleport( endLoc );
                    getWizardry().getWizardPlayer( player.getUniqueId() ).removeSpell( signName );
                    cancel();
                    return;
                } else if ( newLoc.getY() > player.getLocation().getY() ) {
                    player.setVelocity( new Vector(0, 0.5,0) );
                    if ( player.getLocation().add( 0, 2, 0).getBlock().getType().isSolid() ) {
                        player.teleport( player.getLocation().add( 0, 1, 0 ) );
                    }
                } else if ( newLoc.getY() < player.getLocation().getY() ) {
                    player.setVelocity(new Vector(0, -0.5,0));
                    if ( player.getLocation().add( 0,-1,0 ).getBlock().getType().isSolid() ) {
                        player.teleport( player.getLocation().add( 0, -1, 0 ) );
                    }
                } else {
                    player.teleport( destination.getLocation().add( 0, -1, 0 ) );
                }
            }
        }.runTaskTimer( plugin, 5, 1 );
    }

    private Block findDestination( Block start, int direction, Player p ) {
        Block destination = start;
        BlockFace next = direction == 1 ? BlockFace.UP : BlockFace.DOWN;

        while( true ) {
            destination = destination.getRelative( next );
            if ( isElevatorSign( destination ) ) {
                if ( destination.getRelative( BlockFace.DOWN ).getType() == Material.AIR &&
                    destination.getRelative( BlockFace.DOWN ).getRelative( BlockFace.DOWN ).getType().isSolid() ) {
                    break;
                }
            } else if ( destination.getY() == start.getWorld().getMaxHeight() || destination.getY() == 0 ) {
                p.sendMessage( "No valid destination found!" );
                return null;
            }
        }

        return destination;
    }

    private Boolean isElevatorSign( Block b ) {
        if ( b == null || b.getType() == null ) return false;
        if ( Tag.WALL_SIGNS.isTagged( b.getType() ) ) {
            BlockState state = b.getState();
            Sign sign = (Sign)state;
            String[] lines = sign.getLines();
            if ( lines[0] == null ) {
                return false;
            } else {

                if ( ChatColor.stripColor( lines[0] ).equals( signature ) ) {
                    return true;
                }
            }
        }

        return false;
    }



    public static void handleSignEvent(SignChangeEvent event) {
        String[] lines = event.getLines();
        Player p = event.getPlayer();

        lines[0] = "[" + ChatColor.DARK_PURPLE + "WizardElevator" + ChatColor.BLACK + "]";
        lines[1] = "";
        lines[2] = ChatColor.GREEN + "Wand=UP";
        lines[3] = ChatColor.RED + "Shift+Wand=DOWN";

    }


}
