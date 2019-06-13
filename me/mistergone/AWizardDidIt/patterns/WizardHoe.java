package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.ToolPattern;
import me.mistergone.AWizardDidIt.helpers.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

/**
 * MAGIC HOE
 * Plants whatever's in your off-hand in any contiguous farmland in front of you.
 */

public class WizardHoe extends ToolPattern {
    public WizardHoe() {
        patternName = "Wizard Hoe";
        patterns =  new ArrayList<String[]>();
        toolCost = 1;

        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "WOODEN_HOE", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "STONE_HOE", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "IRON_HOE", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GOLDEN_HOE", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "DIAMOND_HOE", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );

        patternFunction = new PatternFunction() {
            @Override
            public void run() {
                ItemStack hoe = magicChest.getChest().getBlockInventory().getItem( 10 );
                ItemMeta meta = hoe.getItemMeta();

                List<String> loreCheck = meta.getLore();
                if ( meta.getLore() == null ) {
                    ArrayList<String> lore = new ArrayList<String>();
                    lore.add( "Wizard Hoe" );
                    meta.setLore( lore );
                    hoe.setItemMeta( meta );
                    player.sendMessage( ChatColor.GOLD + "This hoe has been empowered!" );
                    SpecialEffects.enchantEffect( magicChest.getChest().getLocation() );

                    int[] skipCenter = { 10 };
                    magicChest.clearPattern( skipCenter );
                } else {
                    player.sendMessage( ChatColor.RED + "This hoe cannot be further empowered!" );
                }
            }
        };

        secondaryFunction = new ToolFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                Map<Material, Material> crops = new HashMap<>();
                crops.put( Material.CARROT, Material.CARROTS );
                crops.put( Material.POTATO, Material.POTATOES );
                crops.put( Material.WHEAT_SEEDS, Material.WHEAT );
                crops.put( Material.BEETROOT_SEEDS, Material.BEETROOTS );

                ItemStack offHand = playerInteractEvent.getPlayer().getInventory().getItemInOffHand();

                Boolean targetFarm = playerInteractEvent.getClickedBlock().getType() == Material.FARMLAND;
                Boolean hasCrop = crops.keySet().contains( offHand.getType() );
                Boolean targetCrop = crops.values().contains( playerInteractEvent.getClickedBlock().getType() );

                if (  ( hasCrop && targetFarm ) || targetCrop ) {
                    if (wizardPlayer.checkMsgCooldown( patternName ) == false) {
                        player.sendMessage(ChatColor.AQUA + "You have invoked Wizard Hoe!");
                        wizardPlayer.addMsgCooldown( patternName, 30);
                    }
                    BlockFace newFace = BlockManager.yawToFace( player.getEyeLocation().getYaw() );

                    Block block = playerInteractEvent.getClickedBlock();
                    for ( int i = 0; i <= 20; i++ ) {
                        offHand = player.getInventory().getItemInOffHand();
                        Boolean isCrop = crops.values().contains( block.getType() );
                        Boolean isFarmland = block.getType() == Material.FARMLAND;
                        if ( ( isFarmland || isCrop ) && wizardPlayer.spendToolUse( toolCost ) ) {
                            Boolean belowAir = block.getRelative( BlockFace.UP ).getType() == Material.AIR;
                            Boolean belowCrop = crops.values().contains( block.getRelative( BlockFace.UP ).getType() );
                            Boolean hasItems = offHand.getType() != Material.AIR;

                            if ( isFarmland && belowCrop ) {
                                if (offHand.getType().equals( block.getRelative( BlockFace.UP ).getType() ) ) {
                                    block.getRelative( BlockFace.UP ).breakNaturally();
                                    belowAir = true;
                                }
                            }

                            if ( belowAir && isFarmland && hasItems ) {
                                block.getRelative( BlockFace.UP ).setType( crops.get( offHand.getType() ) ) ;
                                if ( offHand.getAmount() > 1 ) {
                                    offHand.setAmount( offHand.getAmount() - 1 );
                                } else if ( offHand.getAmount() == 1 ) {
                                    player.getInventory().setItemInOffHand( null );
                                }
                            } else if ( !hasItems && isFarmland ) {
                                break;
                            } else if ( isCrop ) {
                                block.breakNaturally();
                            }

                        } else if ( ( isFarmland || isCrop ) ) {
                            if ( !wizardPlayer.checkMsgCooldown( patternName + "OOM") ) {
                                player.sendMessage( ChatColor.DARK_RED + "You do not have enough Wizard Power to invoke " + patternName );
                                wizardPlayer.addMsgCooldown(patternName + "OOM", 5 );
                            }
                        }
                        block = block.getRelative( newFace );
                    }
                }

            }
        };
    }
}
