package me.mistergone.AWizardDidIt.patterns;
import me.mistergone.AWizardDidIt.MagicPattern;
import me.mistergone.AWizardDidIt.ToolPattern;
import me.mistergone.AWizardDidIt.helpers.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.ToolFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

import java.util.ArrayList;
import java.util.List;

public class WizardAxe extends ToolPattern {
    public WizardAxe() {
        patternName = "Wizard Axe";
        patterns =  new ArrayList<String[]>();
        toolCost = 1;

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
                ItemStack axe = magicChest.getChest().getBlockInventory().getItem( 10 );
                ItemMeta meta = axe.getItemMeta();
                // TODO - Remove this legacy support for updating the old name, "Magic Axe"
                List<String> loreCheck = meta.getLore();
                if ( meta.getLore() == null || ( loreCheck != null && loreCheck.get(0).equals( "Magic Axe" )  ) ) {
                    ArrayList<String> lore = new ArrayList<String>();
                    lore.add( "Wizard Axe" );
                    meta.setLore( lore );
                    axe.setItemMeta( meta );
                    player.sendMessage( ChatColor.GOLD + "This axe has been empowered!" );

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
                if ( !player.isSneaking() ) {
                    // TODO - Add a "normal mode" and a "tree feller mode" (and maybe a "leaf blower" mode?)
                    // TODO - Collect blocks first, then determine cost
                    // TODO - When tree felling, decay leaves in a larger radius if those leaves are far from a log
                    WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                    if ( wizardPlayer.spendWizardPower( toolCost ) ) {
                        player.playSound( player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, .3F, 2F  );
                        Material choppedType = blockBreakEvent.getBlock().getType();
                        String leafString = choppedType.toString().substring( 0, choppedType.toString().length() -4 ) + "_LEAVES";
                        Material leafType = Material.valueOf( leafString );
                        ArrayList<Block> blocks = new ArrayList<>();
                        Block firstBlock = blockBreakEvent.getBlock();
                        blocks.add( firstBlock );
                        for (int i = 0; i < 250 && i <= blocks.size(); i++) {
                            Block b = blocks.get(i);
                            Location loc = b.getLocation();
                            // Let the tool break the first block
                            if ( !b.equals( firstBlock ) ) {
                                b.breakNaturally(player.getInventory().getItemInMainHand());
                            }
                            for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
                                for (int y = loc.getBlockY(); y <= loc.getBlockY() + 1; y++) {
                                    for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
                                        Block blockCheck = loc.getWorld().getBlockAt(x, y, z);
                                        if (blockCheck.getType() == choppedType && !blocks.contains(blockCheck) ) {
                                            blocks.add(blockCheck);
                                        } else if ( blockCheck.getType() == leafType  ) {
                                            blockCheck.breakNaturally();
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        player.sendMessage( "You lack the Wizard Power to use " + patternName + ".");
                    }


                }
            }
        };
    }
}
