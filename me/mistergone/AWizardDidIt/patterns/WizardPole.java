package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.ToolPattern;
import me.mistergone.AWizardDidIt.helpers.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.ToolFunction;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class WizardPole extends ToolPattern {
    public WizardPole() {
        patternName = "Wizard Pole";
        patterns =  new ArrayList<String[]>();
        toolCost = 1;

        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "FISHING_ROD", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );

        patternFunction = new PatternFunction() {
            @Override
            public void run() {
                ItemStack rod = magicChest.getChest().getBlockInventory().getItem( 10 );
                ItemMeta meta = rod.getItemMeta();

                if ( meta.getLore() == null ) {
                    ArrayList<String> lore = new ArrayList<String>();
                    lore.add( "Wizard Pole" );
                    meta.setLore( lore );
                    rod.setItemMeta( meta );
                    player.sendMessage( ChatColor.GOLD + "This fishing rod has been empowered!" );

                    int[] skipCenter = { 10 };
                    magicChest.clearPattern( skipCenter );
                } else {
                    player.sendMessage( ChatColor.RED + "This fishing rod cannot be further empowered!" );
                }
            }
        };

        toolFunction = new ToolFunction() {
            @Override
            public void run() {

            }
        };
    }
}
