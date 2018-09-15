package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Bukkit;
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
        cost = .001;

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                if ( clickedBlock != null ) {
                    WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                    Block target = player.getWorld().getBlockAt( clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ() );

                    if ( clickedBlock.getType() == Material.GRASS_PATH && wizardPlayer.spendWizardPower( cost ) ) {
                        target.setType( Material.GRASS_BLOCK );
                    } else if ( clickedBlock.getType() == Material.GRASS_BLOCK && wizardPlayer.spendWizardPower( cost ) ) {
                        target.setType( Material.GRASS_PATH );
                    } else if ( clickedBlock.getType() == Material.DIRT && wizardPlayer.spendWizardPower( cost ) ) {
                        target.setType( Material.GRASS_PATH );
                    }
                }
            }
        };
    }

}
