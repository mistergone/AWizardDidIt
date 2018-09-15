package me.mistergone.AWizardDidIt;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MagicWand extends ItemStack {
    protected ItemStack wandItem;

    public MagicWand( ItemStack wand ) {
        this.wandItem = wand;
    }

    public Boolean isJustAStick() {
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

    public Boolean isActuallyAWand() {
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

    public ItemStack getMagicWand() {
        return this.wandItem;
    }

    public int getMagicWandLevel() {
        return 0;
    }

}
