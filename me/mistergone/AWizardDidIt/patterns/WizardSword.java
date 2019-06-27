package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.baseClasses.PatternFunction;
import me.mistergone.AWizardDidIt.baseClasses.WeaponFunction;
import me.mistergone.AWizardDidIt.baseClasses.WeaponPattern;
import me.mistergone.AWizardDidIt.helpers.*;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WizardSword extends WeaponPattern {

    public static HashMap<String, Integer> modeCosts;

    public WizardSword() {
        patternName = "Wizard Sword";
        patterns =  new ArrayList<String[]>();
        weaponCost = 0;
        modeCosts = new HashMap<String, Integer >();

        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "WOODEN_SWORD", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "STONE_SWORD", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "IRON_SWORD", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GOLDEN_SWORD", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "DIAMOND_SWORD", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );

        modeCosts.put( "Whirlwind Slash", 5 );
        modeCosts.put( "Lifestealer", 15 );
        modeCosts.put( "Moonblade", 0    );


        patternFunction = new PatternFunction(){
            @Override
            public void run() {
                ItemStack sword = magicChest.getChest().getInventory().getItem( 10 );
                ItemMeta meta = sword.getItemMeta();

                List<String> loreCheck = meta.getLore();
                if ( meta.getLore() == null ) {
                    ArrayList<String> lore = new ArrayList<String>();
                    lore.add( "Wizard Sword" );
                    lore.add( "Mode: Normal" );
                    meta.setLore( lore );
                    sword.setItemMeta( meta );
                    player.sendMessage( ChatColor.GOLD + "This sword has been empowered!" );
                    SpecialEffects.enchantEffect( magicChest.getChest().getLocation() );

                    int[] skipCenter = { 10 };
                    magicChest.clearPattern( skipCenter );
                } else {
                    player.sendMessage( ChatColor.RED + "This sword cannot be further empowered!" );
                }
            }
        };

        weaponFunction= new WeaponFunction() {
            @Override
            public void run() {

            }
        };

        this.weaponModes = new ArrayList<>();
        weaponModes.add( "Mode: Normal" );
        weaponModes.add( "Mode: Whirlwind Slash" );
        weaponModes.add( "Mode: Lifestealer" );
        weaponModes.add( "Mode: Moonblade" );

        secondaryFunction= new WeaponFunction() {
            @Override
            public void run() {
                ItemStack offHand = playerInteractEvent.getPlayer().getInventory().getItemInOffHand();
                if ( MagicWand.isActuallyAWand( offHand ) ) {
                    ItemMeta meta = weapon.getItemMeta();
                    List<String> lore = meta.getLore();
                    if ( lore.size() == 1 ) {
                        lore.add( "Mode: Normal" );
                    } else {
                        int index = weaponModes.indexOf( lore.get( 1 ) ) + 1;
                        if ( index == weaponModes.size() ) {
                            index = 0;
                        }
                        lore.set( 1, weaponModes.get( index ) );
                    }
                    meta.setLore( lore );
                    weapon.setItemMeta( meta );
                    player.sendMessage( ChatColor.GOLD + "Wizard Sword set to " + lore.get( 1 ) );
                }
            }
        };

    }

    public static int getModeCost( String mode ) {
        return (int) modeCosts.get( mode );
    }
}
