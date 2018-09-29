package me.mistergone.AWizardDidIt.patterns;
import me.mistergone.AWizardDidIt.MagicWand;
import me.mistergone.AWizardDidIt.ToolPattern;
import me.mistergone.AWizardDidIt.helpers.BlockManager;
import me.mistergone.AWizardDidIt.helpers.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.ToolFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

import java.util.ArrayList;
import java.util.List;

public class WizardShovel extends ToolPattern {
    public WizardShovel() {
        patternName = "Wizard Shovel";
        patterns =  new ArrayList<String[]>();
        toolCost = 1;

        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "WOODEN_SHOVEL", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "STONE_SHOVEL", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "IRON_SHOVEL", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GOLDEN_SHOVEL", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "DIAMOND_SHOVEL", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );

        patternFunction = new PatternFunction() {
            @Override
            public void run() {
                ItemStack shovel = magicChest.getChest().getBlockInventory().getItem( 10 );
                ItemMeta meta = shovel.getItemMeta();
                List<String> loreCheck = meta.getLore();
                if ( meta.getLore() == null ) {
                    ArrayList<String> lore = new ArrayList<String>();
                    lore.add( "Wizard Shovel" );
                    meta.setLore( lore );
                    shovel.setItemMeta( meta );
                    player.sendMessage( ChatColor.GOLD + "This shovel has been empowered!" );

                    int[] skipCenter = { 10 };
                    magicChest.clearPattern( skipCenter );
                } else {
                    player.sendMessage( ChatColor.RED + "This shovel cannot be further empowered!" );
                }
            }
        };

        toolFunction = new ToolFunction() {
            @Override
            public void run() {
                // Holding shift disables the magic!
                if ( !player.isSneaking() ) {
                    double cost = .001;
                    // If you dig one type of dirt, you've dug `em all!
                    ArrayList<Material> dirtTypes = new ArrayList<>();
                    dirtTypes.add( Material.DIRT );
                    dirtTypes.add( Material.COARSE_DIRT );
                    dirtTypes.add( Material.GRASS_BLOCK );

                    Block firstBlock = blockBreakEvent.getBlock();
                    ArrayList<Block> blockBox = new ArrayList<>();
                    WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );

                    // Get the tool lore
                    List<String> lore = tool.getItemMeta().getLore();
                    if ( lore.size() == 1 || lore.get( 1 ).equals( "Mode: 3x3 (Centered)" ) ) {
                        blockBox = BlockManager.getCubeByRadius( firstBlock, 1 );
                    } else if ( lore.get( 1 ).equals( "Mode: 1x1" ) ) {
                        blockBox.add( firstBlock );
                    } else if ( lore.get( 1 ).equals( "Mode: 1x3 (Facing)" ) ) {
                        blockBox = BlockManager.getSquareBoxFromFace( firstBlock, wizardPlayer.getLastFaceClicked(), 3, 1 );
                    } else if ( lore.get( 1 ).equals( "Mode: 2x3 (Facing)" ) ) {
                        blockBox = BlockManager.getSquareBoxFromFace( firstBlock, wizardPlayer.getLastFaceClicked(), 3, 2 );
                    } else if ( lore.get( 1 ).equals( "Mode: 3x3 (Facing)" ) ) {
                        blockBox = BlockManager.getSquareBoxFromFace( firstBlock, wizardPlayer.getLastFaceClicked(), 3, 3 );
                    }

                    Material brokenMat = firstBlock.getType();
                    for ( Block b : blockBox ) {
                        Boolean dirtMatch = dirtTypes.contains( b.getType()) && dirtTypes.contains( brokenMat );
                        if ( ( b.getType() == brokenMat || dirtMatch ) && !b.equals( firstBlock ) ) {
                            if ( wizardPlayer.spendToolUse( toolCost ) ) {
                                b.breakNaturally(player.getInventory().getItemInMainHand());
                            } else {
                                player.sendMessage( "You lack the Wizard Power to use " + patternName + ".");
                            }
                        }
                    }
                }
            }
        };

        this.toolModes = new ArrayList<>();
        toolModes.add( "Mode: 1x1" );
        toolModes.add( "Mode: 1x3 (Facing)" );
        toolModes.add( "Mode: 2x3 (Facing)" );
        toolModes.add( "Mode: 3x3 (Facing)" );
        toolModes.add( "Mode: 3x3 (Centered)" );


        secondaryFunction = new ToolFunction() {
            @Override
            public void run() {
                ItemStack offHand = playerInteractEvent.getPlayer().getInventory().getItemInOffHand();
                if ( MagicWand.isActuallyAWand( offHand ) ) {
                    ItemMeta meta = tool.getItemMeta();
                    List<String> lore = meta.getLore();
                    if ( lore.size() == 1 ) {
                        lore.add( "Mode: 3x3 (Centered)" );
                    } else {
                        int index = toolModes.indexOf( lore.get( 1 ) ) + 1;
                        if ( index == toolModes.size() ) {
                            index = 0;
                        }
                        lore.set( 1, toolModes.get( index ) );
                    }
                    meta.setLore( lore );
                    tool.setItemMeta( meta );
                    player.sendMessage( ChatColor.GOLD + "Wizard Shovel set to " + lore.get( 1 ) );
                }

            }
        };
    }

}
