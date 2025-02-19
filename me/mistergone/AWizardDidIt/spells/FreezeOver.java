package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.baseClasses.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.BlockHelper;
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

public class FreezeOver extends MagicSpell {
    public FreezeOver() {
        spellName = "Freeze Over";
        cost = 20;
        reagents = new ArrayList<String>();
        reagents.add( "SNOWBALL" );

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                Location pLoc = player.getLocation();
                Block feet = player.getWorld().getBlockAt( pLoc );
                Block head = feet.getRelative( BlockFace.UP );
                Block targetBlock = player.getTargetBlock( null, 10 );
                ArrayList<Block> blockBox = new ArrayList<>();
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );

                if ( targetBlock.getType() == Material.WATER ) {
                    if ( !wizardPlayer.spendWizardPower( cost, spellName ) ) return;
                    if ( targetBlock.getRelative( BlockFace.UP ).getType() == Material.AIR ) {
                        blockBox = BlockHelper.getSquareBoxFromFace( targetBlock, BlockFace.UP, 3, 1 );
                        for ( Block block : blockBox ) {
                            if ( block.getType() == Material.WATER && !block.equals( feet ) && !block.equals( head ) ) {
                                block.setType( Material.ICE );
                            }
                        }
                        SpecialEffects.magicPoof( targetBlock.getLocation() );
                        if ( wizardPlayer.checkMsgCooldown( spellName ) == false ) {
                            wizardPlayer.spellAlertWithCooldown( spellName, "",10 );
                        }
                    } else {
                        if ( wizardPlayer.checkMsgCooldown( spellName + " (warn)" ) == false ) {
                            player.sendTitle("", ChatColor.RED + "Freeze Over does not work underwater!");
                        }
                    }
                }
            }
        };
    }
}
