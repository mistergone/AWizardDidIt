package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.baseClasses.MagicPattern;
import me.mistergone.AWizardDidIt.baseClasses.PatternFunction;
import me.mistergone.AWizardDidIt.baseClasses.ToolFunction;
import me.mistergone.AWizardDidIt.helpers.WandHelper;
import me.mistergone.AWizardDidIt.baseClasses.ToolPattern;
import me.mistergone.AWizardDidIt.helpers.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class WizardPick extends ToolPattern {

    public WizardPick() {
        patternName = "Wizard Pick";
        keys = new Material[]{ Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE };
        patterns =  new HashMap<String, String[]>();
        toolCost = 3;

        patterns.put( "Wizard Pick", new String[]
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

                ItemStack pickaxe = magicChest.getChest().getInventory().getItem( 10 );
                ItemMeta meta = pickaxe.getItemMeta();

                List<String> loreCheck = meta.getLore();
                if ( meta.getLore() == null ) {
                    ArrayList<String> lore = new ArrayList<String>();
                    lore.add( "Wizard Pick" );
                    lore.add( "Mode: 3x3 (Centered)" );
                    meta.setLore( lore );
                    pickaxe.setItemMeta( meta );
                    player.sendMessage( ChatColor.GOLD + "This pickaxe has been empowered!" );
                    SpecialEffects.enchantEffect( magicChest.getChest().getLocation() );

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
                        blockBox = BlockHelper.getCubeByRadius(firstBlock, 1);
                    } else if (lore.get(1).equals("Mode: 1x1")) {
                        blockBox.add(firstBlock);
                    } else if (lore.get(1).equals("Mode: 1x3 (Facing)")) {
                        blockBox = BlockHelper.getSquareBoxFromFace(firstBlock, wizardPlayer.getLastFaceClicked(), 3, 1);
                    } else if (lore.get(1).equals("Mode: 2x3 (Facing)")) {
                        blockBox = BlockHelper.getSquareBoxFromFace(firstBlock, wizardPlayer.getLastFaceClicked(), 3, 2);
                    } else if (lore.get(1).equals("Mode: 3x3 (Facing)")) {
                        blockBox = BlockHelper.getSquareBoxFromFace(firstBlock, wizardPlayer.getLastFaceClicked(), 3, 3);
                    }

                    Material brokenMat = blockBreakEvent.getBlock().getType();
                    Location loc = blockBreakEvent.getBlock().getLocation();
                    for (Block b : blockBox) {
                        Boolean sameType = b.getType() == brokenMat;
                        Boolean digType = stoneTypes.contains(b.getType()) && stoneTypes.contains(brokenMat);
                        // Let the tool break the original block
                        if ((sameType || digType) && !b.equals(firstBlock) ) {

                            if ( !wizardPlayer.spendToolUse( toolCost, patternName ) ) return;
                            if ( !BlockHelper.isSafeToBreak( b, player, patternName ) ) continue;

                            b.breakNaturally( player.getInventory().getItemInMainHand() );
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
                    player.sendMessage( ChatColor.GOLD + "Wizard Pick set to " + lore.get( 1 ) );
                }

            }
        };
    }

}
