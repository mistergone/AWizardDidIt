package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.MagicPattern;
import me.mistergone.AWizardDidIt.helpers.PatternFunction;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
                HashMap<Material, Chest> chestIndex = new HashMap<>();
                // create the chestIndex
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

                int movedItems = 0;
                int leftoverItems = 0;
                int failedMoves = 0;
                for ( int i = 3; i <= 26; i ++ ) {

                    ItemStack item = magicChest.getChest().getBlockInventory().getContents()[i];
                    if ( item != null && chestIndex.keySet().contains( item.getType() ) ) {
                        Chest chest = chestIndex.get( item.getType() );
                        Inventory inv = chest.getInventory();
                        int startingAmt = item.getAmount();
                        HashMap<Integer, ItemStack> leftovers = inv.addItem( item );
                        if ( leftovers.size() > 0 ) {
                            ItemStack left = leftovers.get(0);
                            if ( left.getAmount() == startingAmt ) {
                                failedMoves++;
                            } else {
                                leftoverItems++;
                                movedItems++;
                            }
                            item.setAmount( left.getAmount() );
                        } else {
                            magicChest.getChest().getBlockInventory().setItem( i, null );
                            movedItems++;
                        }
                    }

                    if ( i == 8 || i == 17 ) {
                        i += 3;
                    }
                }
                if ( movedItems > 0 || failedMoves > 0 ) {
                    player.playSound( player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_WOLOLO, 3F, 2F );
                    player.sendMessage( "You have invoked " + patternName + "!" );
                    String message = "";
                    if ( movedItems > 0 ) message += String.valueOf( movedItems) + " item stack(s) were moved. ";
                    if ( leftoverItems > 0 ) message += String.valueOf( leftoverItems ) + " item stack(s) had leftover items." ;
                    if ( failedMoves > 0 ) message += String.valueOf( failedMoves ) + " item stack(s) could not be moved.";
                    player.sendMessage( message );
                }
            }


        };

    }
}
