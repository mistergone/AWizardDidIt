package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.baseClasses.CraftFunction;
import me.mistergone.AWizardDidIt.baseClasses.CraftPattern;
import me.mistergone.AWizardDidIt.baseClasses.MagicPattern;
import me.mistergone.AWizardDidIt.helpers.CraftHelper;
import me.mistergone.AWizardDidIt.helpers.WandHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class CraftWand extends CraftPattern {
    public CraftWand() {
        patternName = "Craft Wand";
        keys = new Material[]{ Material.STICK };
        patterns = new HashMap<String, String[]>();
        patterns.put( "Craft Wand", new String[]{
                "BONE_MEAL", "BONE_MEAL", "BONE_MEAL",
                "BONE_MEAL", "STICK", "BONE_MEAL",
                "BONE_MEAL", "BONE_MEAL", "BONE_MEAL"
        } );

        craftFunction = new CraftFunction() {
            @Override
            public void run() {
                String[] pattern = CraftHelper.getMatrixString( craftInventory.getMatrix() );
                String name = MagicPattern.getPatternName( pattern, patterns );
                if ( name == null ) return;
                ItemStack result = new ItemStack( Material.STICK, 1 );
                result = WandHelper.turnIntoWand( result );
                craftInventory.setResult( result );
                wizardPlayer.setWizardLevel(1);
            }
        };

    }
}
