package me.mistergone.AWizardDidIt.listeners;

import me.mistergone.AWizardDidIt.AWizardDidIt;
import me.mistergone.AWizardDidIt.Wizardry;
import me.mistergone.AWizardDidIt.baseClasses.WeaponFunction;
import me.mistergone.AWizardDidIt.baseClasses.WeaponPattern;
import me.mistergone.AWizardDidIt.helpers.*;
import me.mistergone.AWizardDidIt.patterns.WizardBow;
import me.mistergone.AWizardDidIt.patterns.WizardCrossbow;
import me.mistergone.AWizardDidIt.patterns.WizardTrident;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

public class WeaponListener implements Listener {
    private Wizardry wizardry;

    public WeaponListener( Wizardry wizardry ) {
        this.wizardry = wizardry;
    }

    @EventHandler
    public void onProjectileLaunch( ProjectileLaunchEvent event ) {

        // Wizard Projectile handler
        if ( event.getEntity() instanceof Trident || event.getEntity() instanceof Arrow ) {
            AbstractArrow proj = (AbstractArrow) event.getEntity();
            if ( proj.getShooter() instanceof  Player ) {
                Player p = (Player) proj.getShooter();
                ItemStack item = p.getInventory().getItemInMainHand();
                if ( item != null ) {
                    if ( item.getItemMeta() != null && item.getItemMeta().getLore() != null ) {
                        // If trident has a mode, set mode to the projectile.
                        List<String> lore = item.getItemMeta().getLore();
                        Material itemType = item.getType();

                        String loreOne = lore.get(0);
                        if ( loreOne.contains( "Wizard " ) && lore.get(1) != null ) {
                            String loreTwo = lore.get(1);
                            if ( loreTwo.substring( 0, 6 ).equals( "Mode: " ) ) {
                                String mode = loreTwo.substring( 6 );
                                if ( mode.contains( "Teletransference") ) {
                                    AWizardDidIt plugin = (AWizardDidIt) Bukkit.getServer().getPluginManager().getPlugin("AWizardDidIt");
                                    proj.setMetadata( "vector", new FixedMetadataValue( plugin, proj.getVelocity() ) );
                                    proj.setMetadata( "location", new FixedMetadataValue( plugin, proj.getLocation() ) );
                                }

                                if ( itemType == Material.TRIDENT && loreOne.equals( "Wizard Trident" ) ) {
                                    if ( !mode.equals( "Normal" ) ) {
                                        WizardPlayer wizardPlayer = Wizardry.getWizardry().getWizardPlayer( p.getUniqueId() );
                                        int cost = WizardTrident.getModeCost( mode );
                                        if ( cost > wizardPlayer.getWizardPower() ) {
                                            wizardPlayer.spendWizardPower( cost, "Wizard Trident (" + mode + ")" );
                                            return;
                                        }
                                        proj.setCustomName( mode );
                                    }
                                } else if ( itemType == Material.CROSSBOW && loreOne.equals( "Wizard Crossbow" ) ) {
                                    if ( !mode.equals( "Normal" ) ) {
                                        WizardPlayer wizardPlayer = Wizardry.getWizardry().getWizardPlayer( p.getUniqueId() );
                                        int cost = WizardCrossbow.getModeCost( mode );
                                        if ( cost > wizardPlayer.getWizardPower() ) {
                                            wizardPlayer.spendWizardPower( cost, "Wizard Crossbow (" + mode + ")" );
                                            return;
                                        }
                                        proj.setCustomName( mode );
                                    }
                                } else if ( itemType == Material.BOW && loreOne.equals( "Wizard Bow" ) ) {
                                    if ( !mode.equals( "Normal" ) ) {
                                        WizardPlayer wizardPlayer = Wizardry.getWizardry().getWizardPlayer( p.getUniqueId() );
                                        int cost = WizardBow.getModeCost( mode );
                                        if ( cost > wizardPlayer.getWizardPower() ) {
                                            wizardPlayer.spendWizardPower( cost, "Wizard Bow (" + mode + ")" );
                                            return;
                                        }
                                        proj.setCustomName( mode );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent( PlayerInteractEvent e ) {
        Player p = e.getPlayer();
        EquipmentSlot h = e.getHand();
        Action action = e.getAction();
        if (h != null && h == EquipmentSlot.HAND && ( action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR ) ) {
            ItemStack offhand = p.getInventory().getItemInOffHand();
            ItemStack main = p.getInventory().getItemInMainHand();
            // This only works if the offhand is a MagicWand
            if ( !MagicWand.isActuallyAWand( offhand ) ) return;

            if ( offhand != null && main.getItemMeta() != null ) {
                List<String> lore = main.getItemMeta().getLore();
                if ( lore != null ) {
                    String loreCheck = lore.get(0);
                    WeaponPattern weaponPattern = wizardry.getWeaponByLore(loreCheck);
                    if ( weaponPattern != null && weaponPattern.getSecondaryFunction() != null ) {
                        e.setCancelled( true );
                        try {
                            WeaponFunction weaponFunction = weaponPattern.getSecondaryFunction();
                            weaponFunction.weapon = main;
                            weaponFunction.playerInteractEvent = e;
                            weaponFunction.player = p;
                            weaponFunction.call();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
