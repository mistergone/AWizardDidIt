package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.BlockManager;
import me.mistergone.AWizardDidIt.helpers.SpecialEffects;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class LayerLayer extends MagicSpell {

    public LayerLayer() {
        spellName = "Layer Layer";
        cost = 0;
        reagents = new ArrayList<String>();
        reagents.add( "COBBLESTONE_SLAB" );
        reagents.add( "SANDSTONE_SLAB" );
        reagents.add( "COBBLESTONE_WALL" );
        reagents.add( "STONE_SLAB");

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                int layerSlot = player.getInventory().getHeldItemSlot() + 1;
                ItemStack layerItem = player.getInventory().getItem( layerSlot );
                Material layerType = layerItem.getType();
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
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
                    Boolean replaceAll = reagent.getType() == Material.COBBLESTONE_SLAB;
                    Boolean silkTouch = false;

                    // Define the blockBox
                    if ( reagent.getType() == Material.COBBLESTONE_WALL ) {
                        Block targetBlock = clickedBlock.getRelative(BlockFace.UP, 2);
                        // If you didn't click the top face, then the clicked block is the bottom row
                        if ( event.getBlockFace() != BlockFace.UP ) {
                            targetBlock = clickedBlock.getRelative( BlockFace.UP, 1 );
                        }
                        if (reagent.getAmount() == 1) {
                            blockBox.add( targetBlock.getRelative( BlockFace.DOWN ) );
                            blockBox.add( targetBlock);
                            blockBox.add( targetBlock.getRelative( BlockFace.UP ) );
                        } else {
                            blockBox = BlockManager.getSquareBoxFromFace(targetBlock, facing, 3, 1);
                        }
                        replaceAll = true;
                    } else if ( reagent.getType() ==  Material.STONE_SLAB ){
                        blockBox.add( clickedBlock );
                        replaceAll = true;
                        silkTouch = true;
                    } else {
                        Block targetBlock = clickedBlock.getRelative( facing, 2 );
                        if ( reagent.getAmount() == 1 ) {
                            blockBox.add( clickedBlock.getRelative( facing, 1 ) );
                            blockBox.add( targetBlock );
                            blockBox.add( targetBlock.getRelative( facing, 1 ) );
                        } else {
                            blockBox = BlockManager.getSquareBoxFromFace( targetBlock, BlockFace.UP, 3, 1 );
                        }
                    }

                    // Handle the blockBox
                    for ( Block b : blockBox ) {
                        // Check if player ran out of layerItem
                        if ( b != null && player.getInventory().getItem( layerSlot ).getType() != Material.AIR ) {
                            Boolean isAir = BlockManager.airTypes.contains( b.getType() );
                            Boolean sameType = layerType == b.getType();
                            if ( replaceAll  ) {
                                if ( !sameType ) {
                                    if ( !silkTouch ) {
                                        b.breakNaturally();
                                    } else {
                                        if ( BlockManager.silkyPickTypes.contains( b.getType() ) ) {
                                            ItemStack drop = new ItemStack( b.getType() );
                                            loc.getWorld().dropItem( loc, drop );
                                        } else {
                                            b.breakNaturally();
                                        }
                                    }
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

                            SpecialEffects.magicPoof( clickedBlock.getLocation() );
                            if ( wizardPlayer.checkMsgCooldown( spellName ) == false ) {
                                player.sendMessage(ChatColor.DARK_PURPLE + "You have invoked " + spellName + "!");
                                wizardPlayer.addMsgCooldown( spellName, 30 );
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
