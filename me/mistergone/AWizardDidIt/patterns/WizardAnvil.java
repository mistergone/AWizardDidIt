package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.baseClasses.MagicPattern;
import me.mistergone.AWizardDidIt.helpers.ExpHelper;
import me.mistergone.AWizardDidIt.baseClasses.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class WizardAnvil extends MagicPattern {
    public WizardAnvil() {
        patternName = "Wizard Anvil";
        keys = new Material[]{ Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL };

        patterns = new HashMap<String, String[]>();
        patterns.put("Wizard Anvil (Wizard Power)", new String[]
                {"GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "ANY", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST"});
        patterns.put("Wizard Anvil (Exp)", new String[]
                {"BONE_MEAL", "BONE_MEAL", "BONE_MEAL",
                        "BONE_MEAL", "ANY", "BONE_MEAL",
                        "BONE_MEAL", "BONE_MEAL", "BONE_MEAL"});

        patternFunction = new PatternFunction() {
            @Override
            public void run() {
                String[] pattern = magicChest.getPattern();
                String name = MagicPattern.getPatternName( pattern, patterns );

                if ( name == null ) {
                    player.sendMessage(ChatColor.RED + "No magic pattern was found inside this chest!");
                    return;
                }

                player.sendMessage(ChatColor.AQUA + "Invoking " + name + "." );
                Inventory chestInv = magicChest.getChest().getInventory();
                for (int i = 3; i < chestInv.getSize(); i++) {
                    ItemStack item = chestInv.getContents()[i];

                    if (item != null && item.getItemMeta() instanceof Damageable ) {
                        ItemMeta meta = item.getItemMeta();
                        int damage = ((Damageable) meta).getDamage();
                        if ( damage > 0 ) {
                            int max = item.getType().getMaxDurability();
                            String iName = meta.getDisplayName();
                            if ( iName.equals( "" ) ) {
                                iName = item.getType().name();
                                iName = ItemHelper.prettyItemName( iName );
                            }
                            String strDam = String.valueOf( damage );
                            if ( name.equals( "Wizard Anvil (Exp)" ) ) {
                                int cost = (int)Math.ceil( (double)damage / 2 );
                                if ( ExpHelper.spendExp( player, cost ) ) {
                                    ((Damageable) meta).setDamage( 0 );
                                    item.setItemMeta( meta );
                                    player.sendMessage(ChatColor.AQUA + "Your magic repaired " + strDam + " damage from " + iName );
                                } else {
                                    int exp = ExpHelper.getExpTotal( player );
                                    player.sendMessage( ChatColor.RED + "You do not have sufficient EXP to fix " + strDam + " damage on " + iName );
                                    if ( exp > 0 && ExpHelper.spendExp( player, exp ) ) {
                                        ((Damageable) meta).setDamage( damage - ( exp * 2 ) );
                                        item.setItemMeta( meta );
                                        player.sendMessage( ChatColor.AQUA + "Your magic partially repaired " + String.valueOf( exp * 2 ) + " damage (out of " + strDam + " damage) on " + iName );
                                    }
                                    break;
                                }
                            } else if ( name.equals( "Wizard Anvil (Wizard Power)" ) ) {
                                int cost = damage * 2;
                                if ( wizardPlayer.spendWizardPower( cost, null ) ) {
                                    ((Damageable) meta).setDamage( 0 );
                                    item.setItemMeta( meta );
                                    player.sendMessage(ChatColor.AQUA + "Your magic repaired " + strDam +
                                            " damage from " + iName );
                                } else {
                                    int power = wizardPlayer.getWizardPower();
                                    player.sendMessage( ChatColor.RED + "You do not have sufficient Wizard Power to fix " + strDam + " damage on " + iName );
                                    if ( power > 0 && wizardPlayer.spendWizardPower( power, null ) ) {
                                        int repair = (int)Math.ceil((double)power / 2 );
                                        ((Damageable) meta).setDamage( damage - repair );
                                        item.setItemMeta( meta );
                                        player.sendMessage( ChatColor.AQUA + "Your magic partially repaired " + String.valueOf( repair ) + " damage (out of " + strDam + " damage) on " + iName );
                                    }
                                    break;
                                }
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
