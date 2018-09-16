package me.mistergone.AWizardDidIt.patterns;

import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.sun.org.apache.xpath.internal.operations.Bool;
import me.mistergone.AWizardDidIt.MagicPattern;
import me.mistergone.AWizardDidIt.MagicWand;
import me.mistergone.AWizardDidIt.helpers.MagicFunction;
import me.mistergone.AWizardDidIt.helpers.PatternFunction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Function;

import static sun.misc.PostVMInitHook.run;

public class EnchantWand extends MagicPattern {

    public EnchantWand() {
        patternName = "Enchant Wand";

        patterns =  new ArrayList<String[]>();
        patterns.add( new String[]
                { "REDSTONE", "NONE", "REDSTONE",
                "NONE", "REDSTONE", "NONE",
                "REDSTONE", "NONE", "REDSTONE" } );

        patternFunction = new PatternFunction() {
            @Override
            public void run( ) {
                if ( MagicWand.isJustAStick( magicWand ) ) {
                    player.sendMessage(ChatColor.BLUE + "I see you have a taste for magic!");

                    magicWand.addUnsafeEnchantment( Enchantment.MENDING, 0 );
                    ItemMeta meta = magicWand.getItemMeta();
                    meta.addItemFlags( ItemFlag.HIDE_ENCHANTS );
                    ArrayList<String> lore = new ArrayList<String>();
                    lore.add( "A magic wand!" );
                    meta.setLore( lore );
                    meta.setDisplayName( "Magic Wand" );
                    meta.setLocalizedName( "0MAGICWAND" + UUID.randomUUID().toString() );
                    magicWand.setItemMeta( meta );
                    player.sendMessage( ChatColor.GOLD + "Your stick has been turned into a magic wand!");

                    magicChest.clearPattern( );

                } else {
                    player.sendMessage( ChatColor.RED + "Your wand is already enchanted!");
                }
            }
        };


    }

}
