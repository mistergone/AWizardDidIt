package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.baseClasses.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.SpecialEffects;
import me.mistergone.AWizardDidIt.baseClasses.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

/**
 * GRASS CUTTER
 * A spell that cuts the grass. You know, real wizard stuff.
 */

public class GrassCutter extends MagicSpell {

    public GrassCutter() {
        spellName = "Grass Cutter";
        cost = 0;
        reagents = new ArrayList<String>();
        reagents.add( "WHEAT_SEEDS" );

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                SpecialEffects.magicPoof( clickedBlock.getLocation() );
                if ( wizardPlayer.checkMsgCooldown( spellName ) == false ) {
                    wizardPlayer.spellAlert( spellName, "" );
                }


                player.playSound( player.getLocation(), Sound.ENTITY_PARROT_IMITATE_ELDER_GUARDIAN, .6F, .02F  );
                Location loc = player.getLocation();

                int radius = 5;

                List<Block> blocks = new ArrayList<Block>();
                for ( int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++ ) {
                    for( int y = loc.getBlockY() - radius; y <= loc.getBlockY() + radius; y++ ) {
                        for ( int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++ ) {
                            Block block = loc.getWorld().getBlockAt( x, y, z );
                            if ( block.getType() == Material.SHORT_GRASS || block.getType() == Material.TALL_GRASS || block.getType() == Material.FERN || block.getType() == Material.LARGE_FERN ) {
                                block.breakNaturally();
                            }
                        }
                    }
                }


            }
        };
    }

}
