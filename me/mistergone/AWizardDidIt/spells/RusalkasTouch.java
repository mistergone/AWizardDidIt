package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.baseClasses.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.BlockManager;
import me.mistergone.AWizardDidIt.helpers.SpecialEffects;
import me.mistergone.AWizardDidIt.baseClasses.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

/**
 * Rusalka's Touch
 * A spell that lets the player control water, kinda
 */

public class RusalkasTouch extends MagicSpell {

    public RusalkasTouch() {
        spellName = "Rusalka's Touch";
        cost = 1;
        reagents = new ArrayList<String>();
        reagents.add( "BUCKET" );
        reagents.add( "WATER_BUCKET" );

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                if ( !wizardPlayer.spendWizardPower( cost, spellName ) ) return;
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
                    SpecialEffects.magicPoof( clickedBlock.getLocation() );
                    if ( wizardPlayer.checkMsgCooldown( spellName ) == false ) {
                        player.sendMessage(ChatColor.DARK_PURPLE + "You have invoked " + spellName + "!");
                        wizardPlayer.addMsgCooldown( spellName, 30 );
                    }
            }
        };
    }

}
