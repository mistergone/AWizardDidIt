package me.mistergone.AWizardDidIt.helpers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

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
        if ( !isStick  || amt > 1 ) {
            return false;
        } else {
            ItemMeta meta = wandItem.getItemMeta();
            Boolean hasWandLore = false;
            if ( meta.getLore() != null && meta.getLore().get(0).equals( "A magic wand!" ) ) {
                hasWandLore = true;
            }
            Boolean hasLocName = false;
            if ( meta.hasLocalizedName() && meta.getLocalizedName().substring( 1, 10 ).equals( "MAGICWAND" ) ) {
                hasLocName = true;
            }

            if ( hasWandLore && hasLocName ) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static void enchantWand( Player player, Block block ) {
        ItemStack magicWand = player.getInventory().getItemInMainHand();
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
            SpecialEffects.magicPoof( block.getLocation() );
            player.playSound( player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1.5F  );
        }
    }

}
