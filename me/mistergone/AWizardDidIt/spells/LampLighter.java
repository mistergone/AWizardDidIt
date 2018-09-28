package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * LAMP LIGHTER
 * A spell that puts torches where you need them.
 */

public class LampLighter extends MagicSpell {
    public LampLighter() {
        spellName = "Lamp Lighter";
        cost = 1;
        reagents = new ArrayList<String>();
        reagents.add( "TORCH" );
        ArrayList<Material> airTypes = new ArrayList<>();
        airTypes.add( Material.AIR );
        airTypes.add( Material.CAVE_AIR );
        airTypes.add( Material.VOID_AIR );

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                Block torchSpot = null;
                Location loc = player.getLocation();
                if ( loc.getBlock().getType() == Material.TORCH ) {
                    for ( int r = 0; torchSpot == null && r <= 10; r++ ) {
                        for ( int x = -1 * r; x <= r; x++ ) {
                            for ( int y = -1 * r; y <= r/2; y++ ) {
                                for ( int z = -1 * r; z <= r; z++ ) {
                                    if ( torchSpot == null && Math.abs( x ) == r || Math.abs( z ) == r ) {
                                        Block b = player.getWorld().getBlockAt( loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z );
                                        if (b.getLightFromBlocks() <= 7 && airTypes.contains( b.getType() ) && b.getRelative( BlockFace.DOWN ).getType().isSolid() ) {
                                            torchSpot = b;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if ( torchSpot != null ) {
                        torchSpot.setType( Material.TORCH );
                        ItemStack offHand = player.getInventory().getItemInOffHand();
                        if ( offHand.getAmount() > 1 ) {
                            offHand.setAmount( offHand.getAmount() - 1 );
                        } else if ( offHand.getAmount() == 1 ) {
                            player.getInventory().setItemInOffHand( null );
                        }
                        player.sendMessage( "You have invoked Lamp Lighter! A torch has been placed in a dark spot.");
                    } else {
                        player.sendMessage( "Lamp Lighter failed! No valid torch spot was found in range!" );
                    }
                }
            }
        };
    }
}
