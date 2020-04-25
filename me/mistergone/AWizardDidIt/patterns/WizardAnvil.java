package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.baseClasses.MagicPattern;
import me.mistergone.AWizardDidIt.helpers.ExpHelper;
import me.mistergone.AWizardDidIt.baseClasses.PatternFunction;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class WizardAnvil extends MagicPattern {
    public WizardAnvil() {
        patternName = "Wizard Anvil";
        patterns = new ArrayList<String[]>();
        patterns.add(new String[]
                {"GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "ANVIL", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST"});

        patternFunction = new PatternFunction() {
            @Override
            public void run() {
                player.sendMessage(ChatColor.AQUA + "Invoking " + patternName + "." );
                Inventory chestInv = magicChest.getChest().getInventory();
                for (int i = 3; i < chestInv.getSize(); i++) {
                    ItemStack item = chestInv.getContents()[i];

                    if (item != null && item.getItemMeta() instanceof Damageable ) {
                        ItemMeta meta = item.getItemMeta();
                        int damage = ((Damageable) meta).getDamage();
                        if ( damage > 0 ) {
                            int max = item.getType().getMaxDurability();
                            String name = item.getType().toString();
                            String strDam = String.valueOf( damage );
                            if ( ExpHelper.spendExp( player, damage ) ) {
                                ((Damageable) meta).setDamage( 0 );
                                item.setItemMeta( meta );
                                player.sendMessage(ChatColor.AQUA + "Your magic repaired " + strDam + " damage from " + name );
                            } else {
                                int exp = ExpHelper.getExpTotal( player );
                                player.sendMessage( ChatColor.RED + "You do not have sufficient EXP to fix " + strDam + " damage on " + name );
                                if ( exp > 0 && ExpHelper.spendExp( player, exp ) ) {
                                    ((Damageable) meta).setDamage( damage - exp );
                                    item.setItemMeta( meta );
                                    player.sendMessage( ChatColor.AQUA + "Your magic partially repaired " + String.valueOf( exp ) + " damage (out of " + strDam + " damage) on " + name );
                                }
                                break;
                            }
                        }
                    }

                    if (i == 8 || i == 17) {
                        i += 3;
                    }
                }
            }
        };
    }
}
