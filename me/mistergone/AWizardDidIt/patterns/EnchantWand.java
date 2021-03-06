package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.baseClasses.MagicPattern;
import me.mistergone.AWizardDidIt.helpers.WandHelper;
import me.mistergone.AWizardDidIt.baseClasses.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.SpecialEffects;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EnchantWand extends MagicPattern {

    public EnchantWand() {
        patternName = "Enchant Wand";
        keys = new Material[]{ Material.GLOWSTONE_DUST };
        patterns =  new HashMap<String, String[]>();
        patterns.put( "Enchant Wand", new String[]
                { "GLOWSTONE_DUST", "NONE", "GLOWSTONE_DUST",
                "NONE", "GLOWSTONE_DUST", "NONE",
                "GLOWSTONE_DUST", "NONE", "GLOWSTONE_DUST" } );

        patternFunction = new PatternFunction() {
            @Override
            public void run( ) {
                String[] pattern = magicChest.getPattern();
                String name = MagicPattern.getPatternName( pattern, patterns );

                if ( name == null ) {
                    player.sendMessage(ChatColor.RED + "No magic pattern was found inside this chest!");
                    return;
                }

                if ( WandHelper.isJustAStick( magicWand ) ) {
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
                    Location loc = magicChest.getChest().getLocation().add( 0, 1, 0 );
                    SpecialEffects.magicChest( loc );
                    magicChest.clearPattern( );

                } else {
                    player.sendMessage( ChatColor.RED + "Your wand is already enchanted!");
                }
            }
        };


    }

}
