package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.AWizardDidIt;
import me.mistergone.AWizardDidIt.baseClasses.PatternFunction;
import me.mistergone.AWizardDidIt.baseClasses.ToolFunction;
import me.mistergone.AWizardDidIt.helpers.MagicWand;
import me.mistergone.AWizardDidIt.baseClasses.ToolPattern;
import me.mistergone.AWizardDidIt.helpers.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class WizardAxe extends ToolPattern {
    public WizardAxe() {
        patternName = "Wizard Axe";
        patterns =  new ArrayList<String[]>();
        toolCost = 1;
        int lumberjackCost = 5;

        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "WOODEN_AXE", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "STONE_AXE", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "IRON_AXE", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GOLDEN_AXE", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "DIAMOND_AXE", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );

        patternFunction = new PatternFunction() {
            @Override
            public void run() {
                ItemStack axe = magicChest.getChest().getInventory().getItem( 10 );
                ItemMeta meta = axe.getItemMeta();
                // TODO - Remove this legacy support for updating the old name, "Magic Axe"
                List<String> loreCheck = meta.getLore();
                if ( meta.getLore() == null || ( loreCheck != null && loreCheck.get(0).equals( "Magic Axe" )  ) ) {
                    ArrayList<String> lore = new ArrayList<String>();
                    lore.add( "Wizard Axe" );
                    lore.add( "Mode: Lumberjack" );
                    meta.setLore( lore );
                    axe.setItemMeta( meta );
                    player.sendMessage( ChatColor.GOLD + "This axe has been empowered!" );
                    SpecialEffects.enchantEffect( magicChest.getChest().getLocation() );

                    int[] skipCenter = { 10 };
                    magicChest.clearPattern( skipCenter );
                } else {
                    player.sendMessage( ChatColor.RED + "This axe cannot be further empowered!" );
                }
            }
        };

        toolFunction = new ToolFunction() {
            @Override
            public void run() {
                // Holding shift disables the magic!
                if ( player.isSneaking() ) return;
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                Block firstBlock = blockBreakEvent.getBlock();
                Material choppedType = firstBlock.getType();
                ArrayList<Block> blockBox = new ArrayList<>();

                // Get the tool lore
                List<String> lore = tool.getItemMeta().getLore();

                // **** Lumberjack Mode! **** //
                if ( lore.size() == 1 || lore.get(1).equals("Mode: Lumberjack" ) ) {
                    if ( !Tag.LOGS.isTagged( choppedType ) ) return;

                        String leafString = choppedType.toString().substring( 0, choppedType.toString().length() -4 ) + "_LEAVES";
                        Material leafType = Material.valueOf( leafString );
                        ArrayList<Block> blocks = new ArrayList<>();
                        ArrayList<Block> leaves = new ArrayList<>();
                        player.playSound( player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, .3F, 2F  );
                        blocks.add( firstBlock );

                        if ( !wizardPlayer.spendWizardPower( lumberjackCost, patternName ) ) return;

                        for (int i = 0; i < 1000 && i < blocks.size(); i++) {
                            Block b = blocks.get(i);
                            Location loc = b.getLocation();

                            // Let the tool break the first block
                            if ( !b.equals( firstBlock ) ) {
                                b.breakNaturally(player.getInventory().getItemInMainHand());
                            }

                            for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
                                for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++) {
                                    for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
                                        Block blockCheck = loc.getWorld().getBlockAt(x, y, z);
                                        if ( blocks.contains( blockCheck) ) continue;
                                        if ( blockCheck.getType() == choppedType ) {
                                            blocks.add( blockCheck );
                                        } else if ( blockCheck.getType() == leafType  ) {
                                            leaves.add( blockCheck );
                                        }
                                    }
                                }
                            }
                        }

                        Bukkit.getServer().getScheduler().runTaskLater(
                                (AWizardDidIt)Bukkit.getServer().getPluginManager().getPlugin("AWizardDidIt"),
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        removeLeaves( leaves );
                                    }
                                },
                                20
                        );
                } else {

                    // **** "Digging" mode **** //
                    if ( lore.get(1).equals("Mode: 3x3 (Centered)")) {
                        blockBox = BlockManager.getCubeByRadius(firstBlock, 1);
                    } else if (lore.get(1).equals("Mode: 1x1")) {
                        blockBox.add(firstBlock);
                    } else if (lore.get(1).equals("Mode: 1x3 (Facing)")) {
                        blockBox = BlockManager.getSquareBoxFromFace(firstBlock, wizardPlayer.getLastFaceClicked(), 3, 1);
                    } else if (lore.get(1).equals("Mode: 2x3 (Facing)")) {
                        blockBox = BlockManager.getSquareBoxFromFace(firstBlock, wizardPlayer.getLastFaceClicked(), 3, 2);
                    } else if (lore.get(1).equals("Mode: 3x3 (Facing)")) {
                        blockBox = BlockManager.getSquareBoxFromFace(firstBlock, wizardPlayer.getLastFaceClicked(), 3, 3);
                    }

                    Material brokenMat = firstBlock.getType();
                    for ( Block b : blockBox ) {
                        if ( ( b.getType() == brokenMat ) && !b.equals( firstBlock ) ) {
                            if ( !wizardPlayer.spendToolUse( toolCost, patternName ) ) return;
                            b.breakNaturally(player.getInventory().getItemInMainHand());
                        }
                    }

                }
                
            }
        };

        this.toolModes = new ArrayList<>();
        toolModes.add( "Mode: Lumberjack" );
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
                        lore.add( "Mode: Lumberjack" );
                    } else {
                        int index = toolModes.indexOf( lore.get( 1 ) ) + 1;
                        if ( index == toolModes.size() ) {
                            index = 0;
                        }
                        lore.set( 1, toolModes.get( index ) );
                    }
                    meta.setLore( lore );
                    tool.setItemMeta( meta );
                    player.sendMessage( ChatColor.GOLD + "Wizard Axe set to " + lore.get( 1 ) );
                }

            }
        };
    }

    private void removeLeaves( ArrayList<Block> leaves ) {
        if ( leaves  == null || leaves.size() == 0 ) return;
        Material leafType = leaves.get( 0 ).getType();
        for ( int i = 0; i < 2000 && i < leaves.size(); i++ ) {
            Block b = leaves.get( i );
            Location loc = b.getLocation();
            b.breakNaturally();

            for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
                for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++) {
                    for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
                        Block blockCheck = loc.getWorld().getBlockAt(x, y, z);
                        if ( leaves.contains( blockCheck ) ) continue;
                        if ( blockCheck.getType() == leafType ) {
                            Leaves data = (Leaves)blockCheck.getBlockData();
                            if ( data.isPersistent() ) continue;
                            if ( data.getDistance() < 5 ) continue;
                            leaves.add( blockCheck );
                        }
                    }
                }
            }
        }
    }

}
