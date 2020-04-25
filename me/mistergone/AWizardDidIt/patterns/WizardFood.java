package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.baseClasses.MagicPattern;
import me.mistergone.AWizardDidIt.helpers.ExpHelper;
import me.mistergone.AWizardDidIt.baseClasses.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.SpecialEffects;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;


// This class has been deprecated and is no longer active in version 1.3.0
public class WizardFood extends MagicPattern {
    public WizardFood() {
        patternName = "Wizard Food";

        patterns = new ArrayList<String[]>();
        // Cockatrice Cookie
        patterns.add( new String[]
                { "NONE", "COOKIE", "NONE",
                "COOKIE", "COOKIE", "COOKIE",
                "GLOWSTONE_DUST", "FEATHER", "GLOWSTONE_DUST" } );
        // Chicken Soup of Souls
        patterns.add( new String[]
                { "CHICKEN", "BLAZE_POWDER", "CHICKEN",
                "BLAZE_POWDER", "CHICKEN", "BLAZE_POWDER",
                "GLOWSTONE_DUST", "BOWL", "GLOWSTONE_DUST" } );
        // Putrescent Pie
        patterns.add( new String[]
                { "SPIDER_EYE", "SPIDER_EYE", "SPIDER_EYE",
                "ROTTEN_FLESH", "ROTTEN_FLESH", "ROTTEN_FLESH",
                "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );
        // Spectral Sushi
        patterns.add( new String[]
                { "NONE", "NONE", "NONE",
                "TROPICAL_FISH", "TROPICAL_FISH", "TROPICAL_FISH",
                "TROPICAL_FISH", "GLOWSTONE_DUST", "TROPICAL_FISH" } );

        patternFunction = new PatternFunction() {
            @Override
            public void run( ) {
            ItemStack center = magicChest.getChest().getBlockInventory().getContents()[10];
            ItemStack wizardFood = new ItemStack( Material.DIRT );
            ItemMeta meta = center.getItemMeta();
            int expCost = 0;
            WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
            ArrayList<String> lore = new ArrayList<String>();
            String foodName = "ERROR";
            if ( center.getType() == Material.COOKIE ) {
                expCost = 0;
                foodName = "Cockatrice Cookies";
                lore.add( "Restores 50 Wizard Points" );
                wizardFood.setType( Material.COOKIE );
                wizardFood.setAmount( 4 );
            } else if ( center.getType() == Material.CHICKEN ) {
                expCost = 200;
                foodName = "Chicken Soup of Souls";
                lore.add( "Restores 1000 Wizard Points");
                wizardFood.setType( Material.BEETROOT_SOUP );
                wizardFood.setAmount( 1 );
            } else if ( center.getType() == Material.ROTTEN_FLESH ) {
                foodName = "Putrescent Pie";
                lore.add( "Restores 300 Wizard Points." );
                lore.add( "Tastes bad.");
                wizardFood.setType( Material.PUMPKIN_PIE );
                wizardFood.setAmount( 3 );
            } else if ( center.getType() == Material.TROPICAL_FISH ) {
                foodName = "Spectral Sushi";
                lore.add( "Restores 100 Wizard Points." );
                lore.add( "Some side-effects.");
                wizardFood.setType( Material.TROPICAL_FISH );
                wizardFood.setAmount( 5 );
            }
            if ( expCost == 0 || ExpHelper.spendExp( player, expCost ) ) {
                SpecialEffects.enchantEffect( magicChest.getChest().getLocation() );
                wizardFood.addUnsafeEnchantment( Enchantment.MENDING, 1 );
                meta.addItemFlags( ItemFlag.HIDE_ENCHANTS );
                meta.setLore( lore );
                meta.setDisplayName( foodName );
                meta.setLocalizedName( "WIZARDFOOD" );
                wizardFood.setItemMeta( meta );
                wizardFood.addUnsafeEnchantment( Enchantment.MENDING, 1 );
                player.getWorld().dropItem( player.getLocation(), wizardFood );
                player.sendMessage( ChatColor.GOLD + "You have created the Wizard Food, \""
                        + foodName + "\".");
                magicChest.clearPattern();
            } else {
                player.sendMessage( ChatColor.RED + "You do not have sufficient Experience Points to create this Wizard Food recipe.");
            }
            }
        };
    }

    public static Boolean isWizardFood( ItemStack food ) {
        ArrayList<Material> foodList = new ArrayList<>();
        foodList.add( Material.COOKIE );
        foodList.add( Material.PUMPKIN_PIE );
        foodList.add( Material.BEETROOT_SOUP );
        foodList.add( Material.TROPICAL_FISH );

        if ( foodList.contains( food.getType() ) && food.getItemMeta() != null ) {
            ItemMeta meta = food.getItemMeta();
            if ( meta.getLore() != null && meta.getLocalizedName().equals( "WIZARDFOOD") ) {
                return true;
            }
        }

        return false;
    }

    public static void eatWizardFood(ItemStack food, WizardPlayer wizardPlayer ) {
        if ( isWizardFood( food ) ) {
            ItemMeta meta = food.getItemMeta();
            String foodName = meta.getDisplayName();
            if ( foodName.equals( "Cockatrice Cookies" ) ) {
                wizardPlayer.gainWizardPower( 50 );
            } else if ( foodName.equals( "Chicken Soup of Souls" ) ) {
                wizardPlayer.gainWizardPower( 1000 );
            } else if ( foodName.equals( "Putrescent Pie" ) ) {
                wizardPlayer.gainWizardPower(300 );
                PotionEffect hunger = new PotionEffect( PotionEffectType.HUNGER, 60, 80 );
                PotionEffect poison = new PotionEffect( PotionEffectType.POISON, 100, 1 );
                wizardPlayer.getPlayer().addPotionEffect( poison );
                wizardPlayer.getPlayer().addPotionEffect( hunger );
            } else if ( foodName.equals( "Spectral Sushi" ) ) {
                wizardPlayer.gainWizardPower( 100 );
                ArrayList<PotionEffect> effects = new ArrayList<>();
                effects.add( new PotionEffect( PotionEffectType.CONFUSION, 200, 1 ) );
                effects.add( new PotionEffect( PotionEffectType.LEVITATION, 200, 1 ) );
                effects.add( new PotionEffect( PotionEffectType.SLOW_DIGGING, 1200, 4 ) );
                effects.add( new PotionEffect( PotionEffectType.WEAKNESS, 1200, 2 ) );

                // Apply some random effects!
                int count = (int)Math.floor( Math.random() * 2 ) + 1;
                Player player = wizardPlayer.getPlayer();
                for ( int i = 0; i < count; i++ ) {
                    int index = (int)Math.floor( effects.size() * Math.random() );
                    PotionEffect effect = effects.get( index );
                    if ( player.hasPotionEffect( effect.getType() ) ) {
                        player.removePotionEffect( effect.getType() );
                    }
                    player.addPotionEffect( effect );
                    effects.remove( index );
                }

            }
        }

    }

}
