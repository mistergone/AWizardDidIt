package me.mistergone.AWizardDidIt.helpers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WandHelper {

    public static Boolean isJustAStick( ItemStack wandItem) {
        Boolean isStick = wandItem.getType() == Material.STICK;
        int amt = wandItem.getAmount();
        ItemMeta meta = wandItem.getItemMeta();
        Boolean hasLore = meta.getLore() != null;

        if ( isStick && amt == 1 && !hasLore ) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean isJustABone( ItemStack wandItem) {
        Boolean isBone = wandItem.getType() == Material.BONE;
        int amt = wandItem.getAmount();
        ItemMeta meta = wandItem.getItemMeta();
        Boolean hasLore = meta.getLore() != null;

        if ( isBone && amt == 1 && !hasLore ) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean isActuallyAWand( ItemStack wandItem ) {
        Boolean isStick = wandItem.getType() == Material.STICK;
        int amt = wandItem.getAmount();
        if ( !isStick  || amt > 1 ) {
            return false;
        } else {
            ItemMeta meta = wandItem.getItemMeta();
            Boolean hasWandLore = false;
            if ( meta.getLore() != null && meta.getLore().get(0).equals( "A magic wand!" ) ) {
                hasWandLore = true;
            }
            Boolean hasLocName = false;
            if ( meta.hasLocalizedName() && meta.getLocalizedName().substring( 1, 10 ).equals( "MAGICWAND" ) ) {
                hasLocName = true;
            }

            if ( hasWandLore && hasLocName ) {
                return true;
            } else {
                return false;
            }
        }
    }

}
