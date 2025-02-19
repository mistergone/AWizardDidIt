package me.mistergone.AWizardDidIt.helpers;

import me.mistergone.AWizardDidIt.Wizardry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class WandHelper {

    public static Boolean isJustAStick( ItemStack wandItem) {
        Boolean isStick = wandItem.getType() == Material.STICK;
        int amt = wandItem.getAmount();
        ItemMeta meta = wandItem.getItemMeta();
        Boolean hasLore = meta.getLore() != null;

        if ( isStick && amt == 1 && !hasLore ) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean isJustABone( ItemStack wandItem) {
        Boolean isBone = wandItem.getType() == Material.BONE;
        int amt = wandItem.getAmount();
        ItemMeta meta = wandItem.getItemMeta();
        Boolean hasLore = meta.getLore() != null;

        if ( isBone && amt == 1 && !hasLore ) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean isActuallyAWand( ItemStack wandItem ) {
        Boolean isStick = wandItem.getType() == Material.STICK;
        int amt = wandItem.getAmount();
        if ( !isStick ) {
            return false;
        } else if ( wandItem.getAmount() > 1 ) {
            return false;
        } else {
            ItemMeta meta = wandItem.getItemMeta();
            Boolean hasWandLore = false;
            if ( meta.getLore() != null && meta.getLore().get(0).equals( "A magic wand!" ) ) {
                hasWandLore = true;
            }
            Boolean hasEnchant = false;
            if ( meta.hasEnchantmentGlintOverride() == true ) {
                hasEnchant = true;
            }
            Boolean hasRarity = false;
            if ( meta.getRarity() ==  ItemRarity.UNCOMMON ) {
                hasRarity = true;
            }
            if ( hasWandLore && hasEnchant && hasRarity) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static void enchantWand( Player player, Block block ) {
        ItemStack magicWand = player.getInventory().getItemInMainHand();
        if ( WandHelper.isJustAStick( magicWand ) ) {
            player.sendTitle("", ChatColor.BLUE + "I see you have a taste for magic!");
            magicWand = turnIntoWand( magicWand );
            player.getInventory().setItemInMainHand( magicWand );
            player.sendMessage( ChatColor.GOLD + "Your stick has been turned into a magic wand!");
            Wizardry.getWizardry().getWizardPlayer( player.getUniqueId() ).setWizardLevel( 1 );
            SpecialEffects.magicPoof( block.getLocation() );
            player.playSound( player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1.5F  );
        }
    }

    public static ItemStack turnIntoWand( ItemStack stick ) {
        ItemMeta meta = stick.getItemMeta();
        meta.setEnchantmentGlintOverride( true );
        meta.setRarity( ItemRarity.UNCOMMON );
        ArrayList<String> lore = new ArrayList<String>();
        lore.add( "A magic wand!" );
        meta.setLore( lore );
        meta.setDisplayName( "Magic Wand" );
        meta.setMaxStackSize( 1 );
        stick.setItemMeta( meta );
        return stick;
    }

}
