package me.mistergone.AWizardDidIt.baseClasses;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MagicChest {

    private Chest chest;

    public MagicChest( Chest chest ) {
        this.chest = chest;
    }

    public Chest getChest() {
        return this.chest;
    }

    public Material getKey() {
        ItemStack[] stuff = this.chest.getInventory().getContents();
        if ( stuff[10] == null ) {
            return null;
        }
        return stuff[10].getType();
    }

    public String[] getPattern() {
        ItemStack[] stuff = this.chest.getInventory().getContents();

        String[] pattern = new String[9];
        for ( int i = 0; i < 9; i++ ) {
            int slot = i;
            if ( i > 2 ) {
                slot = slot + 6;
            }
            if ( i > 5 ) {
                slot = slot + 6;
            }
            pattern[i] = checkSlot( stuff, slot );
        }

        return pattern;
    }

    public void clearPattern( ) {
        int[] slots = { 0, 1, 2, 9, 10, 11, 18, 19, 20 };

        for ( int slot: slots ) {
            ItemStack item = chest.getInventory().getItem( slot );
            if ( item != null ) {
                if ( item.getAmount() > 1 ) {
                    item.setAmount( item.getAmount() - 1 );
                } else if ( item.getAmount() == 1 ) {
                    chest.getInventory().setItem( slot, null );
                }
            }

        }
    }

    public void clearPattern( int[] ignoreSlots ) {
        int[] defaultSlots = { 0, 1, 2, 9, 10, 11, 18, 19, 20 };
        List<Integer> slots = new ArrayList<>();
        if ( ignoreSlots != null ) {
            for ( int i = 0; i < defaultSlots.length; i++ ) {
                int testSlot = defaultSlots[i];
                Boolean contains = Arrays.stream( ignoreSlots ).anyMatch( j -> j == testSlot );
                if ( !contains ) {
                    slots.add( testSlot );
                }
            }
        }
        for ( int slot: slots ) {
            ItemStack item = chest.getInventory().getContents()[slot];
            if ( item != null ) {
                if ( item.getAmount() > 1 ) {
                    item.setAmount( item.getAmount() - 1 );
                } else if ( item.getAmount() == 1 ) {
                    chest.getInventory().setItem( slot, null );
                }
            }

        }
    }

    private String checkSlot( ItemStack[] stuff, int index ) {
        String mat = new String();
        ItemStack slot = stuff[index];
        if ( slot == null ) {
            return "NONE";
        } else {
            mat = slot.getType().toString();
        }
        return mat;
    }
}
