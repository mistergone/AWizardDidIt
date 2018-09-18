package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.MagicPattern;
import me.mistergone.AWizardDidIt.MagicWand;
import me.mistergone.AWizardDidIt.ToolPattern;
import me.mistergone.AWizardDidIt.helpers.BlockBoxer;
import me.mistergone.AWizardDidIt.helpers.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.ToolFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class WizardPick extends ToolPattern {

    public WizardPick() {
        patternName = "Wizard Pick";
        patterns =  new ArrayList<String[]>();
        toolCost = 1;

        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                "GLOWSTONE_DUST", "WOODEN_PICKAXE", "GLOWSTONE_DUST",
                "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                "GLOWSTONE_DUST", "STONE_PICKAXE", "GLOWSTONE_DUST",
                "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                "GLOWSTONE_DUST", "IRON_PICKAXE", "GLOWSTONE_DUST",
                "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                "GLOWSTONE_DUST", "GOLDEN_PICKAXE", "GLOWSTONE_DUST",
                "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                "GLOWSTONE_DUST", "DIAMOND_PICKAXE", "GLOWSTONE_DUST",
                "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );

        patternFunction = new PatternFunction() {
            @Override
            public void run() {
                ItemStack pickaxe = magicChest.getChest().getBlockInventory().getItem( 10 );
                ItemMeta meta = pickaxe.getItemMeta();

                // TODO - Remove this legacy support for updating the old name, "Magic Shovel"
                List<String> loreCheck = meta.getLore();
                if ( meta.getLore() == null || ( loreCheck != null && loreCheck.get(0).equals( "Magic Pick" )  )) {
                    ArrayList<String> lore = new ArrayList<String>();
                    lore.add( "Wizard Pick" );
                    meta.setLore( lore );
                    pickaxe.setItemMeta( meta );
                    player.sendMessage( ChatColor.GOLD + "This pickaxe has been empowered!" );

                    int[] skipCenter = { 10 };
                    magicChest.clearPattern( skipCenter );
                } else {
                    player.sendMessage( ChatColor.RED + "This pickaxe cannot be further empowered!" );
                }
            }
        };

        toolFunction = new ToolFunction() {
            @Override
            public void run() {
                // Holding shift disables the magic!
                if (!player.isSneaking()) {
                    ArrayList<Material> stoneTypes = new ArrayList<>();
                    // If player digs any type of stone, count it as all stone
                    stoneTypes.add(Material.STONE);
                    stoneTypes.add(Material.GRANITE);
                    stoneTypes.add(Material.DIORITE);
                    stoneTypes.add(Material.ANDESITE);
                    stoneTypes.add(Material.COBBLESTONE);

                    Block firstBlock = blockBreakEvent.getBlock();
                    ArrayList<Block> blockBox = new ArrayList<>();
                    WizardPlayer wizardPlayer = getWizardry().getWizardPlayer(player.getUniqueId());

                    // Get the tool lore
                    List<String> lore = tool.getItemMeta().getLore();
                    if (lore.size() == 1 || lore.get(1).equals("Mode: 3x3 (Centered)")) {
                        blockBox = BlockBoxer.getCubeByRadius(firstBlock, 1);
                    } else if (lore.get(1).equals("Mode: 1x1")) {
                        blockBox.add(firstBlock);
                    } else if (lore.get(1).equals("Mode: 1x3 (Facing)")) {
                        blockBox = BlockBoxer.getSquareBoxFromFace(firstBlock, wizardPlayer.getLastFaceClicked(), 3, 1);
                    } else if (lore.get(1).equals("Mode: 2x3 (Facing)")) {
                        blockBox = BlockBoxer.getSquareBoxFromFace(firstBlock, wizardPlayer.getLastFaceClicked(), 3, 2);
                    } else if (lore.get(1).equals("Mode: 3x3 (Facing)")) {
                        blockBox = BlockBoxer.getSquareBoxFromFace(firstBlock, wizardPlayer.getLastFaceClicked(), 3, 3);
                    }

                    Material brokenMat = blockBreakEvent.getBlock().getType();
                    Location loc = blockBreakEvent.getBlock().getLocation();
                    if ( blockBox.size() < 4 || wizardPlayer.spendWizardPower( toolCost ) ) {
                        for (Block b : blockBox) {
                            Boolean sameType = b.getType() == brokenMat;
                            Boolean digType = stoneTypes.contains(b.getType()) && stoneTypes.contains(brokenMat);
                            // Let the tool break the original block
                            if ((sameType || digType) && !b.equals(firstBlock) ) {
                                b.breakNaturally(player.getInventory().getItemInMainHand());
                            }
                        }
                    } else {
                        player.sendMessage( "You lack the Wizard Power to use " + patternName + ".");
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
