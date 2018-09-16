package me.mistergone.AWizardDidIt.patterns;

import com.mysql.fabric.xmlrpc.base.Array;
import me.mistergone.AWizardDidIt.MagicPattern;
import me.mistergone.AWizardDidIt.ToolPattern;
import me.mistergone.AWizardDidIt.helpers.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.ToolFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MAGIC HOE
 * Plants whatever's in your off-hand in any contiguous farmland in front of you.
 */

public class WizardHoe extends ToolPattern {
    public WizardHoe() {
        patternName = "Wizard Hoe";
        patterns =  new ArrayList<String[]>();

        patterns.add( new String[]
                { "REDSTONE", "REDSTONE", "REDSTONE",
                        "REDSTONE", "WOODEN_HOE", "REDSTONE",
                        "REDSTONE", "REDSTONE", "REDSTONE" } );
        patterns.add( new String[]
                { "REDSTONE", "REDSTONE", "REDSTONE",
                        "REDSTONE", "STONE_HOE", "REDSTONE",
                        "REDSTONE", "REDSTONE", "REDSTONE" } );
        patterns.add( new String[]
                { "REDSTONE", "REDSTONE", "REDSTONE",
                        "REDSTONE", "IRON_HOE", "REDSTONE",
                        "REDSTONE", "REDSTONE", "REDSTONE" } );
        patterns.add( new String[]
                { "REDSTONE", "REDSTONE", "REDSTONE",
                        "REDSTONE", "GOLDEN_HOE", "REDSTONE",
                        "REDSTONE", "REDSTONE", "REDSTONE" } );
        patterns.add( new String[]
                { "REDSTONE", "REDSTONE", "REDSTONE",
                        "REDSTONE", "DIAMOND_HOE", "REDSTONE",
                        "REDSTONE", "REDSTONE", "REDSTONE" } );

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
                crops.put( Material.POTATO, Material.POTATOES);
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
                    int yaw = Math.abs( Math.round( player.getLocation().getYaw() ) );
                    BlockFace newFace;
                    if ( yaw <= 45 || yaw >= 315 ) {
                        newFace = BlockFace.SOUTH;
                    } else if ( yaw > 45 && yaw < 135 ) {
                        newFace = BlockFace.EAST;
                    } else if ( yaw >=135 && yaw < 225 ) {
                        newFace = BlockFace.NORTH;
                    } else {
                        newFace = BlockFace.WEST;
                    }

                    Block block = playerInteractEvent.getClickedBlock();
                    for ( int i = 0; i <= 20; i++ ) {
                        offHand = player.getInventory().getItemInOffHand();
                        Boolean isCrop = crops.values().contains( block.getType() );
                        Boolean isFarmland = block.getType() == Material.FARMLAND;
                        if ( isFarmland || isCrop ) {
                            Boolean belowAir = block.getRelative( BlockFace.UP ).getType() == Material.AIR;
                            Boolean belowCrop = crops.values().contains( block.getRelative( BlockFace.UP ).getType() );
                            Boolean hasItems = offHand.getType() != Material.AIR;

                            if ( isFarmland && belowCrop ) {
                                if (offHand.getType().equals( block.getRelative( BlockFace.UP ).getType() ) ) {
                                    block.getRelative( BlockFace.UP ).breakNaturally();
                                    belowAir = true;
                                }
                            }

                            if ( belowAir && isFarmland && hasItems && wizardPlayer.spendWizardPower( .001 ) ) {
                                block.getRelative( BlockFace.UP ).setType( crops.get( offHand.getType() ) ) ;
                                if ( offHand.getAmount() > 1 ) {
                                    offHand.setAmount( offHand.getAmount() - 1 );
                                } else if ( offHand.getAmount() == 1 ) {
                                    player.getInventory().setItemInOffHand( null );
                                }
                            } else if ( !hasItems && isFarmland ) {
                                break;
                            } else if ( isCrop && wizardPlayer.spendWizardPower( .001 ) ) {
                                block.breakNaturally();
                            }

                        }
                        block = block.getRelative( newFace );
                    }
                }

            }
        };
    }
}
