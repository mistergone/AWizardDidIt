package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.baseClasses.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.SpecialEffects;
import me.mistergone.AWizardDidIt.baseClasses.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

/**
 * ROAD TO NOWHERE
 * A spell that lets the player toggle the presence of a path on a grass or dirt block
 */

public class RoadToNowhere extends MagicSpell {

    public RoadToNowhere() {
        spellName = "Road to Nowhere";
        reagents = new ArrayList<String>();
        reagents.add( "WOODEN_SHOVEL" );
        reagents.add( "STONE_SHOVEL" );
        reagents.add( "IRON_SHOVEL" );
        reagents.add( "GOLDEN_SHOVEL" );
        reagents.add( "DIAMOND_SHOVEL" );
        cost = 1;

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                if ( clickedBlock != null ) {
                    WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                    Block target = player.getWorld().getBlockAt( clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ() );
                    if ( clickedBlock.getType() == Material.DIRT_PATH ) {
                        if ( !wizardPlayer.spendToolUse( cost, spellName ) ) return;
                        target.setType( Material.GRASS_BLOCK );
                        SpecialEffects.magicPoof( clickedBlock.getLocation() );
                    } else if ( clickedBlock.getType() == Material.GRASS_BLOCK ) {
                        if ( !wizardPlayer.spendToolUse( cost, spellName ) ) return;
                        target.setType( Material.DIRT_PATH );
                        SpecialEffects.magicPoof( clickedBlock.getLocation() );
                    } else if ( clickedBlock.getType() == Material.DIRT ) {
                        if ( !wizardPlayer.spendToolUse( cost, spellName ) ) return;
                        target.setType( Material.DIRT_PATH );
                        SpecialEffects.magicPoof( clickedBlock.getLocation() );
                    }
                }
            }
        };
    }

}
