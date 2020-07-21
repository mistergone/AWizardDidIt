package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.baseClasses.MagicPattern;
import me.mistergone.AWizardDidIt.baseClasses.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.SpecialEffects;
import me.mistergone.AWizardDidIt.helpers.WandHelper;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class WizardDust extends MagicPattern {

    public WizardDust() {
        patternName = "Wizard Dust";
        keys = new Material[]{ Material.BONE_MEAL };
        patterns =  new HashMap<String, String[]>();
        patterns.put( "Wizard Dust", new String[]
                {"NONE", "NONE", "NONE",
                        "NONE", "BONE_MEAL", "NONE",
                        "NONE", "NONE", "NONE"});

        patternFunction = new PatternFunction() {
            @Override
            public void run() {
                String[] pattern = magicChest.getPattern();
                String name = MagicPattern.getPatternName( pattern, patterns );

                if ( name == null ) {
                    player.sendMessage(ChatColor.RED + "No magic pattern was found inside this chest!");
                    return;
                }

                if ( WandHelper.isJustAStick(magicWand) || WandHelper.isActuallyAWand( magicWand )) {
                    WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                    if ( !wizardPlayer.spendWizardPower( 150 , patternName ) ) return;
                    player.sendMessage(ChatColor.BLUE + "You have invoked " + patternName + "!");

                    ItemStack bonemeal = magicChest.getChest().getInventory().getItem( 10 );
                    if ( bonemeal.getAmount() > 1 ) {
                        bonemeal.setAmount( bonemeal.getAmount() - 1 );
                    } else {
                        magicChest.clearPattern();
                    }
                    ItemStack dust = new ItemStack( Material.GLOWSTONE_DUST );
                    dust.setAmount( 1 );
                    player.getWorld().dropItem( player.getLocation(), dust );

                    Location loc = magicChest.getChest().getLocation().add(0, 1, 0);
                    SpecialEffects.magicChest(loc);

                }
            }
        };
    }
}