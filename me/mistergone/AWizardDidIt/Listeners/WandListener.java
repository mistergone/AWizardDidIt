package me.mistergone.AWizardDidIt.Listeners;

import me.mistergone.AWizardDidIt.*;
import me.mistergone.AWizardDidIt.helpers.*;
import me.mistergone.AWizardDidIt.patterns.EnchantWand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class WandListener implements Listener {
    private Wizardry wizardry;

    public WandListener( Wizardry wizardry ) {
        this.wizardry = wizardry;
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void PlayerInteractEvent(PlayerInteractEvent e){
        Player p = e.getPlayer();
        EquipmentSlot h = e.getHand();

        if(  h != null && h == EquipmentSlot.HAND ) {
            if ( e.getItem() != null && e.getItem().getType() == Material.STICK && e.getItem().getAmount() == 1 ) {
                MagicWand magicWand = new MagicWand( e.getItem() );
                WizardPlayer wizardPlayer = wizardry.getWizardPlayer( e.getPlayer().getUniqueId() );

                // If a stick is used on a chest, it might be a Magic Pattern
                if ( e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.CHEST ) {
                    e.setCancelled( true );
                    Chest chest = (org.bukkit.block.Chest) e.getClickedBlock().getState();
                    MagicChest magicChest = new MagicChest( chest );
                    String[] pattern = magicChest.getPattern();

                    List<String> list = Arrays.asList( pattern );
                    if (list.contains("TOOMANY")) {
                        p.sendMessage(ChatColor.RED + "A magic pattern cannot contain stacked items!");
                    }

                    MagicPattern magicPattern = wizardry.getPatternFinder().matchPattern( pattern );

                    Boolean wandOrEnchant = magicPattern instanceof EnchantWand || magicWand.isActuallyAWand();

                    // Run the MagicFunction
                    if ( magicPattern != null && magicPattern.getMagicFunction() != null && wandOrEnchant ) {
                        try {
                            PatternFunction function = magicPattern.getMagicFunction();
                            function.setPlayer( p );
                            function.setMagicWand( magicWand );
                            function.setMagicChest( magicChest );
                            function.call();
                        } catch( Exception ex ) {
                            ex.printStackTrace( );
                        }

                    } else if ( magicPattern == null ) {
                        p.sendMessage( ChatColor.RED + "No magic pattern was found inside this chest!" );
                    } else if ( !magicWand.isActuallyAWand() ) {
                        p.sendMessage( ChatColor.RED + "You are not wielding a magic wand!" );
                    }

                // If you just wave a magic wand around, magic might happen!
                } else if ( magicWand.isActuallyAWand() ){
                    ItemStack offItem = p.getInventory().getItemInOffHand();
                    MagicSpell magicSpell = null;
                    if ( offItem == null || offItem.getType() == Material.AIR ) {
                        wizardPlayer.showWizardBar();
                    } else {
                        magicSpell = wizardry.getSpellFinder().matchReagent( offItem.getType().toString() );
                        if ( magicSpell == null ) {
                            p.sendMessage( ChatColor.RED + "No spell found for this reagent!" );
                        } else if ( magicSpell != null && magicSpell.getSpellFunction() != null ) {
                            try {
                                SpellFunction function = magicSpell.getSpellFunction();
                                function.setPlayer( p );
                                function.setMagicWand( magicWand );
                                function.setReagent( offItem );
                                function.call();
                            } catch( Exception ex ) {
                                ex.printStackTrace( );
                            }

                        }
                    }

                }

            }

        }

    }
}
