package me.mistergone.AWizardDidIt.baseClasses;

import org.bukkit.inventory.ItemStack;

public class CraftPattern extends MagicPattern {
    public ItemStack resultItem;

    public CraftFunction craftFunction;
    public CraftFunction getCraftFunction() {
        return this.craftFunction;
    }
}
