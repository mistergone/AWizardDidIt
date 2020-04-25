package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.baseClasses.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.BlockHelper;
import me.mistergone.AWizardDidIt.helpers.SpecialEffects;
import me.mistergone.AWizardDidIt.baseClasses.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Set;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class LayerLayer extends MagicSpell {

    public LayerLayer() {
        spellName = "Layer Layer";
        cost = 0;
        int toolUseCost = 2;
        reagents = new ArrayList<String>();
        // All SLABS are reagents for this spell
        Set<Material> slabs = Tag.SLABS.getValues();
        for ( Material slab : slabs ) {
            reagents.add( slab.toString() );
        }
        // All WALLS are reagents for this spell
        Set<Material> walls = Tag.WALLS.getValues();
        for ( Material wall : walls ) {
            reagents.add( wall.toString() );
        }

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                PlayerInventory pInventory = player.getInventory();
                int layerSlot = pInventory.getHeldItemSlot() + 1;
                ItemStack layerItem = pInventory.getItem( layerSlot );
                Material layerType = null;
                if (layerItem != null) {
                    layerType = layerItem.getType();
                }
                if ( layerItem == null || layerSlot > 8 ) {
                    player.sendMessage( ChatColor.DARK_PURPLE + "Layer-Layer could not be invoked because no item was found in the slot to the right of your Magic Wand.");
                    return;
                }
                if ( !layerType.isSolid() ) {
                    player.sendMessage( ChatColor.DARK_PURPLE + "Layer-Layer could not be invoked because the item found in the slot to the right of your Magic Wand was not a solid block.");
                    return;
                }

                Location loc = player.getLocation();
                ArrayList<Block> blockBox = new ArrayList<>();
                BlockFace facing = BlockHelper.yawToFace( loc.getYaw() );
                Boolean replaceAll = !reagent.getType().toString().contains( "SANDSTONE" );
                Boolean silkTouch = false;
                Boolean lowerSlab = reagent.getType().toString().contains( "SMOOTH" );

                // Define the blockBox
                if ( Tag.WALLS.isTagged( reagent.getType() ) ) {
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
                        blockBox = BlockHelper.getSquareBoxFromFace(targetBlock, facing, 3, 1);
                    }
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
                        blockBox = BlockHelper.getSquareBoxFromFace( targetBlock, BlockFace.UP, 3, 1 );
                    }
                }

                // Handle the blockBox
                for ( Block b : blockBox ) {
                    if ( wizardPlayer.spendToolUse( toolUseCost, spellName ) ) {
                        // Check if player ran out of layerItem
                        if ( b != null && player.getInventory().getItem( layerSlot ) == null ) {
                            for ( int i = layerSlot + 1; i < 9; i++ ) {
                                if ( player.getInventory().getItem( i ) != null && player.getInventory().getItem( i ).getType() == layerType ) {
                                    int count = player.getInventory().getItem( i ).getAmount();
                                    ItemStack moved = new ItemStack( layerType, count );
                                    player.getInventory().setItem( layerSlot, moved );
                                    player.getInventory().setItem( i, null );
                                    break;
                                }
                            }
                        }

                        if ( b != null && player.getInventory().getItem( layerSlot ) != null ) {
                            Boolean isAir = BlockHelper.airTypes.contains( b.getType() );
                            Boolean isSolid = b.getType().isSolid();
                            Boolean sameType = layerType == b.getType();
                            layerItem = player.getInventory().getItem( layerSlot );
                            if ( b.getType() == Material.BEDROCK ) continue;
                            if ( replaceAll  ) {
                                if ( !sameType ) {
                                    if ( !silkTouch ) {
                                        b.breakNaturally();
                                    } else {
                                        if ( BlockHelper.isSilkyPickType( b.getType() ) ) {
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
                            } else if ( isAir || !isSolid ){
                                if ( !isAir ) {
                                    b.breakNaturally();
                                }
                                b.setType( layerType );
                                // Set to upper slab if applicable
                                // Reduce stack
                                if ( layerItem.getAmount() > 1 ) {
                                    layerItem.setAmount( layerItem.getAmount() - 1 );
                                } else if ( layerItem.getAmount() == 1 ) {
                                    player.getInventory().setItem( layerSlot, null );
                                }
                            }
                            // Change slab to upper unless specified otherwise
                            if ( b.getType() == layerType && Tag.SLABS.isTagged( layerType ) && !lowerSlab ) {
                                String state = b.getState().getBlockData().getAsString();
                                state = state.replace( "type=bottom", "type=top" );
                                b.setBlockData( Bukkit.getServer().createBlockData( state ) );
                            }

                            // If layerSlot is null, look for more layerItem in the hotbar, and move it to layerSlot
                            if ( player.getInventory().getItem( layerSlot ) == null ) {
                                for ( int i = layerSlot + 1; i < 9; i++ ) {
                                    if ( player.getInventory().getItem( i ) != null && player.getInventory().getItem( i ).getType() == layerType ) {
                                        int count = player.getInventory().getItem( i ).getAmount();
                                        ItemStack moved = new ItemStack( layerType, count );
                                        player.getInventory().setItem( layerSlot, moved );
                                        player.getInventory().setItem( i, null );
                                        break;
                                    }
                                }
                            }

                            SpecialEffects.magicPoof( clickedBlock.getLocation() );
                            if ( wizardPlayer.checkMsgCooldown( spellName ) == false ) {
                                player.sendMessage(ChatColor.DARK_PURPLE + "You have invoked " + spellName + "!");
                                wizardPlayer.addMsgCooldown( spellName, 5 );
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
