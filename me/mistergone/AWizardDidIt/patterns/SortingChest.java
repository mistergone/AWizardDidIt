package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.MagicPattern;
import me.mistergone.AWizardDidIt.helpers.BlockBoxer;
import me.mistergone.AWizardDidIt.helpers.PatternFunction;
import net.minecraft.server.v1_13_R2.ItemFood;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SortingChest extends MagicPattern {
    public SortingChest() {
        patternName = "Sorting Chest";

        patterns =  new ArrayList<String[]>();
        patterns.add( new String[]
                { "OBSIDIAN", "GLOWSTONE_DUST", "OBSIDIAN",
                    "GLOWSTONE_DUST", "CHEST", "GLOWSTONE_DUST",
                    "OBSIDIAN", "GLOWSTONE_DUST", "OBSIDIAN" } );

        patternFunction = new PatternFunction() {
            @Override
            public void run() {
                Location loc = magicChest.getChest().getLocation();
                HashMap<Material, Chest> chestIndex = new HashMap<>();
                // create the chestIndex
//                ArrayList<Block> blocks = BlockBoxer.getBoxByDimensions( magicChest.getChest().getBlock(), 20, 10, 20 );
                List<Entity> entities = player.getNearbyEntities( 20, 10, 20 );
                for ( Entity e : entities ) {
                    if ( e instanceof ItemFrame ) {
                        Block b = e.getWorld().getBlockAt( e.getLocation() ).getRelative( BlockFace.DOWN );
                        if (  b.getType() == Material.CHEST ) {
                            ItemStack inFrame = ( (ItemFrame)e ).getItem();
                            chestIndex.put( inFrame.getType(), (Chest)b.getState() );
                        }
                    }
                }


                for ( int i = 3; i <= 26; i ++ ) {
                    ItemStack item = magicChest.getChest().getBlockInventory().getContents()[i];
                    if ( item != null && chestIndex.keySet().contains( item.getType() ) ) {
                        Chest chest = chestIndex.get( item.getType() );
                        Inventory inv = chest.getInventory();
                        HashMap<Integer, ItemStack> leftovers = inv.addItem( item );
                        if ( leftovers.size() > 0 ) {
                            ItemStack left = leftovers.get(0);
                            item.setAmount( left.getAmount() );
                        } else {
                            magicChest.getChest().getBlockInventory().setItem( i, null );
                        }

                    }

                    if ( i == 8 || i == 17 ) {
                        i += 3;
                    }
                }
            }
        };

    }
}
