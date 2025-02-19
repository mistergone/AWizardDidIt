package me.mistergone.AWizardDidIt.helpers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class CraftHelper {
    public static String[] getMatrixString( ItemStack[] matrix ) {
        ArrayList<String> results = new ArrayList<>();
        for ( ItemStack item : matrix) {
            results.add( item.getType().toString() );
        }

        return results.toArray( new String[0] );
    }

}
