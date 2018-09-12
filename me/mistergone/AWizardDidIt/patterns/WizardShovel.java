package me.mistergone.AWizardDidIt.patterns;
import me.mistergone.AWizardDidIt.MagicPattern;
import me.mistergone.AWizardDidIt.ToolPattern;
import me.mistergone.AWizardDidIt.helpers.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.ToolFunction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class WizardShovel extends ToolPattern {
    public WizardShovel() {
        patterns =  new ArrayList<String[]>();

        patterns.add( new String[]
                { "REDSTONE", "REDSTONE", "REDSTONE",
                        "REDSTONE", "WOODEN_SHOVEL", "REDSTONE",
                        "REDSTONE", "REDSTONE", "REDSTONE" } );
        patterns.add( new String[]
                { "REDSTONE", "REDSTONE", "REDSTONE",
                        "REDSTONE", "STONE_SHOVEL", "REDSTONE",
                        "REDSTONE", "REDSTONE", "REDSTONE" } );
        patterns.add( new String[]
                { "REDSTONE", "REDSTONE", "REDSTONE",
                        "REDSTONE", "IRON_SHOVEL", "REDSTONE",
                        "REDSTONE", "REDSTONE", "REDSTONE" } );
        patterns.add( new String[]
                { "REDSTONE", "REDSTONE", "REDSTONE",
                        "REDSTONE", "GOLDEN_SHOVEL", "REDSTONE",
                        "REDSTONE", "REDSTONE", "REDSTONE" } );
        patterns.add( new String[]
                { "REDSTONE", "REDSTONE", "REDSTONE",
                        "REDSTONE", "DIAMOND_SHOVEL", "REDSTONE",
                        "REDSTONE", "REDSTONE", "REDSTONE" } );

        patternFunction = new PatternFunction() {
            @Override
            public void run() {
                ItemStack shovel = magicChest.getChest().getBlockInventory().getItem( 10 );
                ItemMeta meta = shovel.getItemMeta();
                // TODO - Remove this legacy support for updating the old name, "Magic Shovel"
                List<String> loreCheck = meta.getLore();
                if ( meta.getLore() == null || ( loreCheck != null && loreCheck.get(0).equals( "Magic Shovel" )  )) {
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
                // If you dig one type of dirt, you've dug `em all!
                ArrayList<Material> dirtTypes = new ArrayList<>();
                dirtTypes.add( Material.DIRT );
                dirtTypes.add( Material.COARSE_DIRT );
                dirtTypes.add( Material.GRASS_BLOCK );

                // Holding shift disables the magic!
                if ( !player.isSneaking() ) {
                    Block firstBlock = blockBreakEvent.getBlock();
                    Material brokenMat = firstBlock.getType();
                    Location loc = blockBreakEvent.getBlock().getLocation();
                    for ( int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++  ) {
                        for ( int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++ ) {
                            for ( int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++ ) {
                                Block block = loc.getWorld().getBlockAt( x, y, z );
                                Boolean dirtMatch = dirtTypes.contains( block.getType()) && dirtTypes.contains( brokenMat );
                                if ( ( block.getType() == brokenMat || dirtMatch ) && !block.equals( firstBlock ) ) {
                                    block.breakNaturally( player.getInventory().getItemInMainHand() );
                                }
                            }
                        }
                    }
                }
            }
        };
    }

}

