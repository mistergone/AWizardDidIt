package me.mistergone.AWizardDidIt.helpers;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemHelper {

    public static Boolean hasWizardLore(ItemStack i ) {
        List<String> lore = i.getItemMeta().getLore();
        if ( lore == null ) return false;
        String loreCheck = lore.get( 0 );
        if ( loreCheck.contains("Wizard ") ) {
            return true;
        }
        return false;
    }

}
