package me.mistergone.AWizardDidIt.baseClasses;

import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CraftFunction extends MagicFunction {
    public CraftingInventory craftInventory;

    public CraftingInventory getInventory() {
        return this.craftInventory;
    }
    public void setInventory( CraftingInventory i ) {
        this.craftInventory = i;
    }
}
