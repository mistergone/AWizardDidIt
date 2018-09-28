package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.BlockManager;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class LayerLayer extends MagicSpell {

    public LayerLayer() {
        spellName = "Cloud Rider";
        cost = 0;
        reagents = new ArrayList<String>();
        reagents.add( "COBBLESTONE_SLAB" );
        reagents.add( "SANDSTONE_SLAB" );

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                int layerSlot = player.getInventory().getHeldItemSlot() + 1;
                ItemStack layerItem = player.getInventory().getItem( layerSlot );
                Material layerType = layerItem.getType();
                if ( layerItem == null || layerSlot > 8 ) {
                    player.sendMessage( ChatColor.DARK_PURPLE + "Layer-Layer could not be invoked because no item was found in the slot to the right of your Magic Wand.");
                    return;
                }
                if ( !layerType.isSolid() ) {
                    player.sendMessage( ChatColor.DARK_PURPLE + "Layer-Layer could not be invoked because the item found in the slot to the right of your Magic Wand was not a solid block.");
                    return;
                }
                if ( player.isOnGround() && clickedBlock.getType().isSolid() ) {
                    Location loc = player.getLocation();
                    ArrayList<Block> blockBox = new ArrayList<>();
                    BlockFace facing = BlockManager.yawToFace( loc.getYaw() );
                    Block targetBlock = clickedBlock.getRelative( facing, 2 );
                    blockBox = BlockManager.getSquareBoxFromFace( targetBlock, BlockFace.UP, 3, 1 );
                    Boolean replaceAll = reagent.getType() == Material.COBBLESTONE_SLAB;

                    for ( Block b : blockBox ) {
                        // Check if player ran out of layerItem
                        if ( b != null && player.getInventory().getItem( layerSlot ).getType() != Material.AIR ) {
                            Boolean isAir = BlockManager.airTypes.contains( b.getType() );
                            Boolean sameType = layerType == b.getType();
                            if ( replaceAll  ) {
                                if ( !sameType ) {
                                    b.breakNaturally();
                                    b.setType( layerType );
                                    // Reduce stack
                                    if ( layerItem.getAmount() > 1 ) {
                                        layerItem.setAmount( layerItem.getAmount() - 1 );
                                    } else if ( layerItem.getAmount() == 1 ) {
                                        player.getInventory().setItem( layerSlot, null );
                                    }
                                }
                            } else if ( isAir ){
                                b.setType( layerType );
                                // Reduce stack
                                if ( layerItem.getAmount() > 1 ) {
                                    layerItem.setAmount( layerItem.getAmount() - 1 );
                                } else if ( layerItem.getAmount() == 1 ) {
                                    player.getInventory().setItem( layerSlot, null );
                                }
                            }
                        } else {
                            return;
                        }

                    }

                }
            }
        };
    }
}
