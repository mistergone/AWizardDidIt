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

import java.util.ArrayList;
import java.util.List;

public class WizardPick extends ToolPattern {

    public WizardPick() {
        patterns =  new ArrayList<String[]>();

        patterns.add( new String[]
                { "REDSTONE", "REDSTONE", "REDSTONE",
                "REDSTONE", "WOODEN_PICKAXE", "REDSTONE",
                "REDSTONE", "REDSTONE", "REDSTONE" } );
        patterns.add( new String[]
                { "REDSTONE", "REDSTONE", "REDSTONE",
                "REDSTONE", "STONE_PICKAXE", "REDSTONE",
                "REDSTONE", "REDSTONE", "REDSTONE" } );
        patterns.add( new String[]
                { "REDSTONE", "REDSTONE", "REDSTONE",
                "REDSTONE", "IRON_PICKAXE", "REDSTONE",
                "REDSTONE", "REDSTONE", "REDSTONE" } );
        patterns.add( new String[]
                { "REDSTONE", "REDSTONE", "REDSTONE",
                "REDSTONE", "GOLDEN_PICKAXE", "REDSTONE",
                "REDSTONE", "REDSTONE", "REDSTONE" } );
        patterns.add( new String[]
                { "REDSTONE", "REDSTONE", "REDSTONE",
                "REDSTONE", "DIAMOND_PICKAXE", "REDSTONE",
                "REDSTONE", "REDSTONE", "REDSTONE" } );

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
                if ( !player.isSneaking() ) {
                    ArrayList<Material> stoneTypes = new ArrayList<>();
                    // If player digs any type of stone, count it as all stone
                    stoneTypes.add( Material.STONE );
                    stoneTypes.add( Material.GRANITE );
                    stoneTypes.add( Material.DIORITE );
                    stoneTypes.add( Material.ANDESITE );
                    stoneTypes.add( Material.COBBLESTONE );

                    Block firstBlock = blockBreakEvent.getBlock();
                    Material brokenMat = blockBreakEvent.getBlock().getType();
                    Location loc = blockBreakEvent.getBlock().getLocation();

                    for ( int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++  ) {
                        for ( int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++ ) {
                            for ( int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++ ) {
                                Block block = loc.getWorld().getBlockAt( x, y, z );
                                Boolean sameType = block.getType() == brokenMat;
                                Boolean digType = stoneTypes.contains( block.getType() ) && stoneTypes.contains( brokenMat );
                                // Let the tool break the original block
                                if ( ( sameType || digType ) && !block.equals( firstBlock ) ) {
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
