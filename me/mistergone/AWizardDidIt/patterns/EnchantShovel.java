package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.baseClasses.MagicPattern;
import me.mistergone.AWizardDidIt.baseClasses.PatternFunction;
import me.mistergone.AWizardDidIt.baseClasses.ToolFunction;
import me.mistergone.AWizardDidIt.helpers.WandHelper;
import me.mistergone.AWizardDidIt.baseClasses.ToolPattern;
import me.mistergone.AWizardDidIt.helpers.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class EnchantShovel extends ToolPattern {
    public EnchantShovel() {
        patternName = "Wizard Shovel";
        keys = new Material[]{ Material.WOODEN_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL,
                Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL };
        patterns =  new HashMap<String, String[]>();
        toolCost = 3;

        patterns.put( "Wizard Shovel", new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "ANY", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );

        patternFunction = new PatternFunction() {
            @Override
            public void run() {
                String[] pattern = magicChest.getPattern();
                String name = MagicPattern.getPatternName( pattern, patterns );

                if ( name == null ) {
                    player.sendMessage(ChatColor.RED + "No magic pattern was found inside this chest!");
                    return;
                }

                ItemStack shovel = magicChest.getChest().getInventory().getItem( 10 );
                ItemMeta meta = shovel.getItemMeta();
                List<String> loreCheck = meta.getLore();
                if ( meta.getLore() == null ) {
                    ArrayList<String> lore = new ArrayList<String>();
                    lore.add( "Wizard Shovel" );
                    lore.add( "Mode: 3x3 (Centered)" );
                    meta.setLore( lore );
                    shovel.setItemMeta( meta );
                    player.sendMessage( ChatColor.GOLD + "This shovel has been empowered!" );
                    SpecialEffects.enchantEffect( magicChest.getChest().getLocation() );

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
                        blockBox = BlockHelper.getCubeByRadius( firstBlock, 1 );
                    } else if ( lore.get( 1 ).equals( "Mode: 1x1" ) ) {
                        blockBox.add( firstBlock );
                    } else if ( lore.get( 1 ).equals( "Mode: 1x3 (Facing)" ) ) {
                        blockBox = BlockHelper.getSquareBoxFromFace( firstBlock, wizardPlayer.getLastFaceClicked(), 3, 1 );
                    } else if ( lore.get( 1 ).equals( "Mode: 2x3 (Facing)" ) ) {
                        blockBox = BlockHelper.getSquareBoxFromFace( firstBlock, wizardPlayer.getLastFaceClicked(), 3, 2 );
                    } else if ( lore.get( 1 ).equals( "Mode: 3x3 (Facing)" ) ) {
                        blockBox = BlockHelper.getSquareBoxFromFace( firstBlock, wizardPlayer.getLastFaceClicked(), 3, 3 );
                    }

                    Material brokenMat = firstBlock.getType();
                    for ( Block b : blockBox ) {
                        Boolean dirtMatch = dirtTypes.contains( b.getType()) && dirtTypes.contains( brokenMat );
                        if ( ( b.getType() == brokenMat || dirtMatch ) && !b.equals( firstBlock ) ) {
                            if ( !wizardPlayer.spendToolUse( toolCost, patternName ) ) return;
                            if ( !BlockHelper.isSafeToBreak( b, player, patternName ) ) continue;

                            b.breakNaturally(player.getInventory().getItemInMainHand());
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
                if ( WandHelper.isActuallyAWand( offHand ) ) {
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

