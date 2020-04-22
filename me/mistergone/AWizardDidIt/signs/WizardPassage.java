package me.mistergone.AWizardDidIt.signs;

import me.mistergone.AWizardDidIt.AWizardDidIt;
import me.mistergone.AWizardDidIt.baseClasses.MagicSign;
import me.mistergone.AWizardDidIt.baseClasses.SignFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class WizardPassage extends MagicSign {

    public WizardPassage() {
        signName = "Wizard Passage";
        signature = "[WizardPassage]";
        cost = 1;

        signFunction = new SignFunction() {
            @Override
            public void run() {
            WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
            BlockFace facing = ( (Directional)signBlock.getBlockData() ).getFacing().getOppositeFace();
            BlockFace relative = getAdjacentBlock( facing, lines[1] );
            Block secondSignBlock = signBlock.getRelative( facing ).getRelative( facing );
            int timer = Integer.valueOf( lines[2] );
            if ( relative == null ) return;
            Block top = signBlock.getRelative( facing ).getRelative( relative );
            Block bottom = top.getRelative( BlockFace.DOWN );

            // Check for second signBlock on the opposite side
            if ( !isWizardPassageSign( secondSignBlock ) || lines[1].equals( ( (Sign)secondSignBlock.getState() ).getLine( 1 ) ) ) {
                secondSignBlock = null;
            }

            // Let's not mess with bedrock
            if ( top.getType() == Material.BEDROCK || bottom.getType() == Material.BEDROCK ) return;

            makePassage( signBlock, top, bottom, timer, secondSignBlock );
            }
        };
    }

    private void makePassage( Block signBlock, Block top, Block bottom, int timer, Block secondSignBlock ) {
        AWizardDidIt plugin = (AWizardDidIt)Bukkit.getServer().getPluginManager().getPlugin("AWizardDidIt");

        Sign state = (Sign)signBlock.getState();
        Sign secondState = secondSignBlock == null ? null : (Sign) secondSignBlock.getState();
        String[] lines = state.getLines();
        String delay = state.getLine(2 );
        String secondDelay = secondState == null ? null : secondState.getLine( 2 );
        Material topType = top.getType();
        Material bottomType = bottom.getType();

        state.setLine( 2, "ACTIVE" );
        state.update();

        if ( secondState != null ) {
            secondState.setLine( 2, "ACTIVE" );
            secondState.update();
        }

        top.setType( Material.AIR );
        bottom.setType( Material.AIR );
        timer = timer * 20;

        BukkitTask task = Bukkit.getServer().getScheduler().runTaskLater(
                plugin,
                new Runnable() {
                    @Override
                    public void run() {
                        top.setType( topType );
                        bottom.setType( bottomType );
                        state.setLine( 2, delay );
                        state.update();
                        if ( secondSignBlock != null ) {
                            secondState.setLine( 2, secondDelay );
                            secondState.update();
                        }
                    }
                },
                timer
        );
    }

    public BlockFace getAdjacentBlock( BlockFace face, String direction ) {
        if ( direction.equals( ">>" ) ) {
            if ( face == BlockFace.NORTH ) return BlockFace.EAST;
            if ( face == BlockFace.EAST ) return BlockFace.SOUTH;
            if ( face == BlockFace.SOUTH ) return BlockFace.WEST;
            if ( face == BlockFace.WEST ) return BlockFace.NORTH;
        } else if ( direction.equals( "<<" ) ) {
            if ( face == BlockFace.NORTH ) return BlockFace.WEST;
            if ( face == BlockFace.WEST ) return BlockFace.SOUTH;
            if ( face == BlockFace.SOUTH ) return BlockFace.EAST;
            if ( face == BlockFace.EAST ) return BlockFace.NORTH;
        }
        return null;
    }

    public static void handleSignEvent(SignChangeEvent event) {
        String[] lines = event.getLines();
        Player p = event.getPlayer();

        if ( !Tag.WALL_SIGNS.isTagged( event.getBlock().getType() ) ) {
            p.sendMessage( ChatColor.RED + "Wizard Passage must be a wall sign!");
            lines[0] = "Wizard Passage must";
            lines[1] = "be a wall sign.";
            return;
        }

        if ( !( lines[1].equals( "<<" ) || lines[1].equals( ">>" ) ) ) {
            lines[1] = ">>";
        }
        if ( ( lines[2].equals( "" ) ) || Integer.valueOf( lines[2] ) > 10 ) {
            lines[2] = "5";
        }

        lines[0] = "[" + ChatColor.DARK_PURPLE + "WizardPassage" + ChatColor.BLACK + "]";
        lines[3] = "";
    }

    public static Boolean isWizardPassageSign( Block b ) {
        if ( !Tag.WALL_SIGNS.isTagged(  b.getType() ) ) return false;
        Sign sign = (Sign) b.getState();
        String[] lines = sign.getLines();
        if ( !( ChatColor.stripColor( lines[0] ). equals( "[WizardPassage]" ) ) ) return false;

        return true;
    }
}
