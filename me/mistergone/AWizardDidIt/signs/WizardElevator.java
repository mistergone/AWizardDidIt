package me.mistergone.AWizardDidIt.signs;

import me.mistergone.AWizardDidIt.AWizardDidIt;
import me.mistergone.AWizardDidIt.baseClasses.MagicSign;
import me.mistergone.AWizardDidIt.baseClasses.SignFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import net.minecraft.server.v1_16_R1.IBlockFragilePlantElement;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class WizardElevator extends MagicSign {

    public WizardElevator() {
        signName = "Wizard Elevator";
        signature = "[WizardElevator]";
        cost = 2;

        signFunction = new SignFunction() {
            @Override
            public void run() {
                if ( signPlacement( signBlock, player ) == false ) return;

                int direction = player.isSneaking() ? -1 : 1;
                Block destination = findDestination( signBlock, direction, player );

                if ( destination == null ) return;

                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                if ( !wizardPlayer.spendWizardPower( cost, signName ) ) return;

                wizardPlayer.addSpell( signName );
                moveIt( wizardPlayer, destination, signBlock );

            }
        };
    }

    private void moveIt( WizardPlayer wizardPlayer, Block destination, Block signBlock ) {
        AWizardDidIt plugin = (AWizardDidIt)Bukkit.getServer().getPluginManager().getPlugin("AWizardDidIt");
        Player p = wizardPlayer.getPlayer();
        final Location newLoc = destination.getLocation();
        final AtomicInteger stuckCount = new AtomicInteger();
        Location startingPoint = signBlock.getLocation().add( 0.5,-1,0.5 );
        startingPoint.setPitch( p.getLocation().getPitch() );
        startingPoint.setYaw( p.getLocation().getYaw() );
        p.teleport( startingPoint );
        p.setAllowFlight( true );

        new BukkitRunnable(){
            @Override
            public void run() {

                p.setFallDistance(0f);

                // Stuck check - sometimes, the player gets stuck under a black, so we check
                // to see if the player is possibly stuck, and teleport them if stuck
                if ( p.getLocation().getY() == wizardPlayer.getLastKnownLocation().getY() ) {
                    stuckCount.incrementAndGet();
                } else {
                    stuckCount.set( 0 );
                }
                if ( stuckCount.intValue() > 10 ) {
                    p.teleport( p.getLocation().add( 0, .5, 0 ) );
                }
                wizardPlayer.setLastKnownLocation( p.getLocation() );

                if ( Math.abs( newLoc.getY() - p.getLocation().getY() ) < .7 ) {
                    Location endLoc = destination.getLocation().add(0.5,-1,0.5);
                    endLoc.setYaw( p.getLocation().getYaw() );
                    endLoc.setPitch( p.getLocation().getPitch() );
                    p.teleport( endLoc );
                   wizardPlayer.removeSpell( signName );
                    if ( p.getGameMode() != GameMode.CREATIVE ) {
                        p.setAllowFlight( false );
                    }
                    cancel();
                    return;
                } else if ( newLoc.getY() > p.getLocation().getY() ) {
                    p.setVelocity( new Vector(0, 0.5,0) );
                    if ( p.getLocation().add( 0, 2, 0).getBlock().getType().isSolid() ) {
                        p.teleport( p.getLocation().add( 0, 1, 0 ) );
                    }
                } else if ( newLoc.getY() < p.getLocation().getY() ) {
                    p.setVelocity(new Vector(0, -0.5,0));
                    if ( p.getLocation().add( 0,-1,0 ).getBlock().getType().isSolid() ) {
                        p.teleport( p.getLocation().add( 0, -1, 0 ) );
                    }
                } else {
                    // Something weird has happened?
                    p.teleport( destination.getLocation().add( 0, -1, 0 ) );
                }
            }
        }.runTaskTimer( plugin, 5, 1 );
    }

    private Block findDestination( Block start, int direction, Player p ) {
        Block destination = start;
        BlockFace next = direction == 1 ? BlockFace.UP : BlockFace.DOWN;

        while( true ) {
            destination = destination.getRelative( next );
            // TODO: Refactor the following IF statement into a function
            if ( destination.getType() == Material.FIRE || destination.getType() == Material.LAVA || destination.getType() == Material.SWEET_BERRY_BUSH ) {
                p.sendMessage(ChatColor.RED + "The elevator path is not safe! No transit in that direction is possible." );
                return null;
            }
            if ( isElevatorSign( destination ) ) {
                Material footType = destination.getRelative( BlockFace.DOWN ).getType();
                Material floorType = destination.getRelative( BlockFace.DOWN ).getRelative( BlockFace.DOWN ).getType();
                if ( !footType.isSolid() && footType != Material.LAVA && footType != Material.FIRE &&
                        floorType.isSolid() && floorType != Material.MAGMA_BLOCK ) {
                    break;
                }
            } else if ( destination.getY() == start.getWorld().getMaxHeight() || destination.getY() == 0 ) {
                p.sendMessage( ChatColor.RED + "No valid destination found!" );
                return null;
            }
        }

        return destination;
    }

    private Boolean signPlacement( Block b, Player p ) {
        Material footType = b.getRelative( BlockFace.DOWN ).getType();
        Material floorType = b.getRelative( BlockFace.DOWN ).getRelative( BlockFace.DOWN ).getType();
        if ( footType.isSolid() ) {
            p.sendMessage( "A WizardElevator sign must be placed exactly one block above the ground!" );
            return false;
        } else if ( footType == Material.LAVA || footType == Material.FIRE ||  floorType == Material.MAGMA_BLOCK ) {
            p.sendMessage( "A WizardElevator sign must not be placed over damaging blocks!" );
            return false;
        } else {
            return true;
        }
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

        if ( !Tag.WALL_SIGNS.isTagged( event.getBlock().getType() ) ) {
            p.sendMessage( ChatColor.RED + "Wizard Elevator must be a wall sign!");
            lines[0] = "Wizard Elevator";
            lines[1] = "must be";
            lines[2] = "a wall sign.";
            return;
        }

        lines[0] = "[" + ChatColor.DARK_PURPLE + "WizardElevator" + ChatColor.BLACK + "]";
        lines[2] = ChatColor.GREEN + "Wand=UP";
        lines[3] = ChatColor.RED + "Shift+Wand=DOWN";

    }


}
