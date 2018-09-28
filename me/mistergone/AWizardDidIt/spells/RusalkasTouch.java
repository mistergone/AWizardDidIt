package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.BlockManager;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;

/**
 * Rusalka's Touch
 * A spell that lets the player control water, kinda
 */

public class RusalkasTouch extends MagicSpell {

    public RusalkasTouch() {
        spellName = "Rusalka's Touch";
        cost = 0;
        reagents = new ArrayList<String>();
        reagents.add( "BUCKET" );
        reagents.add( "WATER_BUCKET" );

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                if ( reagent.getType() == Material.BUCKET ) {
                    // Remove water!
                    Block targetBlock = player.getTargetBlock( null, 10 );
                    if ( targetBlock.getType() == Material.WATER ) {
                        Location loc = player.getLocation();
                        BlockFace face = BlockManager.yawToFace( loc.getYaw() );
                        if ( loc.getPitch() > 45 ) {
                            face = BlockFace.DOWN;
                        } else if ( loc.getPitch() < -45 ) {
                            face = BlockFace.UP;
                        }
                        ArrayList<Block> blockBox = BlockManager.getSquareBoxFromFace( targetBlock, face.getOppositeFace(), 3, 3 );
                        for ( Block block : blockBox ) {
                            if ( block.getType() == Material.WATER ) {
                                block.setType( Material.AIR );
                            }
                        }
                    }

                } else if ( reagent.getType() == Material.WATER_BUCKET ) {
                    // Repair water!
                    Block targetBlock = player.getTargetBlock( null, 10 );
                    if ( targetBlock.getType() == Material.WATER ) {
                        BlockFace face = BlockManager.yawToFace( player.getLocation().getYaw() );
                        ArrayList<Block> blockBox = BlockManager.getSquareBoxFromFace( targetBlock, face.getOppositeFace(), 3, 3 );
                        for ( Block block : blockBox ) {
                            if ( block.getType() == Material.WATER ) {
                                block.setType( Material.WATER );
                            }
                        }
                    }

                }

            }
        };
    }

}
