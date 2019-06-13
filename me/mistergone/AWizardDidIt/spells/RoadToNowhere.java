package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.SpecialEffects;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
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

                    if ( clickedBlock.getType() == Material.GRASS_PATH && wizardPlayer.spendToolUse( cost ) ) {
                        target.setType( Material.GRASS_BLOCK );
                        SpecialEffects.magicPoof( clickedBlock.getLocation() );
                    } else if ( clickedBlock.getType() == Material.GRASS_BLOCK && wizardPlayer.spendToolUse( cost ) ) {
                        target.setType( Material.GRASS_PATH );
                        SpecialEffects.magicPoof( clickedBlock.getLocation() );
                    } else if ( clickedBlock.getType() == Material.DIRT && wizardPlayer.spendToolUse( cost ) ) {
                        target.setType( Material.GRASS_PATH );
                        SpecialEffects.magicPoof( clickedBlock.getLocation() );
                    }
                }
            }
        };
    }

}
