package me.mistergone.AWizardDidIt.listeners;

import me.mistergone.AWizardDidIt.Wizardry;
import me.mistergone.AWizardDidIt.baseClasses.WeaponFunction;
import me.mistergone.AWizardDidIt.baseClasses.WeaponPattern;
import me.mistergone.AWizardDidIt.helpers.*;
import me.mistergone.AWizardDidIt.patterns.WizardSword;
import me.mistergone.AWizardDidIt.patterns.WizardTrident;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class DamageListener implements Listener {

    private Wizardry wizardry;

    public DamageListener( Wizardry wizardry ) {
        this.wizardry = wizardry;
    }

    @EventHandler
    public void onEntityDamage( EntityDamageEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        Boolean isFall = cause == EntityDamageEvent.DamageCause.FALL;
        Boolean isSuff = cause == EntityDamageEvent.DamageCause.SUFFOCATION;
        Boolean isWall = cause == EntityDamageEvent.DamageCause.FLY_INTO_WALL;
        if ( ! ( event.getEntity() instanceof Player) ) return;

        Player player = (Player)event.getEntity();
        WizardPlayer wizardPlayer = wizardry.getWizardPlayer( player.getUniqueId() );
        if ( event.getEntity() instanceof Player && ( isFall || isWall || isSuff) ) {

            if ( wizardPlayer.checkSpell("Mighty Leap") && isFall ) {
                int damageCost = (int)Math.floor( event.getDamage() / 2 );
                if ( wizardPlayer.spendWizardPower( damageCost, null ) ) {
                    event.setDamage( 0 );

                    event.setCancelled( true );
                    if ( wizardPlayer.checkMsgCooldown( "Mighty Leap (Damage)" ) == false ) {
                        wizardPlayer.getPlayer().sendMessage(ChatColor.YELLOW + "The magic of Mighty Leap has protected you from harm... this time...");
                        wizardPlayer.addMsgCooldown( "Mighty Leap (Damage)", 60 );
                    }
                } else {
                    wizardPlayer.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "You lack the Wizard Power to be protected from the fall!");
                }
                wizardPlayer.removeSpell("Mighty Leap");

            } else if ( ( isFall || isSuff || isWall ) && wizardPlayer.checkSpell( "Wizard Elevator" ) ) {
                event.setCancelled( true );
                if ( isFall ) {
                    player.sendMessage( ChatColor.AQUA + "It looks like something went wrong with a Wizard Elevator, so your falling damage was prevented!");
                    wizardPlayer.removeSpell( "Wizard Elevator" );
                }

            } else if ( ( isFall || isSuff || isWall ) && wizardPlayer.checkSpell( "Teletransference" ) ) {
                event.setCancelled( true );
                if ( isFall ) {
                    player.sendMessage( ChatColor.AQUA + "It looks like something went wrong with Teletransference, so your falling damage was prevented!");
                    wizardPlayer.removeSpell( "Teletransference" );
                }

            } else if ( wizardPlayer.checkSpell( "Cloud Rider" ) || wizardPlayer.checkSpell( "Cloud Rider (Gliding)" )
                    || wizardPlayer.checkSpell( "Cloud Rider (Grounded)" ) ) {

                if ( event.getDamage() >= player.getHealth() ) {
                    event.setDamage( player.getHealth() - 0.5 );
                    player.sendMessage(  ChatColor.RED + "Ouch! " + ChatColor.AQUA + "The magic of Cloud Rider has protected you!" );
                }
                if ( wizardPlayer.checkSpell( "Cloud Rider" ) ) {
                    wizardPlayer.removeSpell( "Cloud Rider" );
                }
                if ( wizardPlayer.checkSpell( "Cloud Rider (Gliding)" ) ) {
                    wizardPlayer.removeSpell( "Cloud Rider (Gliding)" );
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if ( event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE ) {
            Projectile projectile = (Projectile) event.getDamager();
            if ( !( projectile.getShooter() instanceof Player ) ) return;
            Player p = (Player) projectile.getShooter();
            WizardPlayer wizardPlayer = Wizardry.getWizardry().getWizardPlayer( p.getUniqueId() );
            Entity damager = event.getDamager();

            // ***** Arrows ***** //
            if ( damager instanceof Arrow) {
                if ( damager.getCustomName() == null ) return;
                String customName = damager.getCustomName();


                if ( customName.equals( "Alf's Action Arrow Projectile" ) ) {
                    if ( event.getEntity() instanceof Monster) {
                        double damage =  Math.floor( ( Math.random() * 6 ) + 5 );
                        event.setDamage( damage );
                    }
                }

                if ( event.getEntity() instanceof Monster ) {
                    if ( customName.equals( "Slow Bolt" ) ) {
                        PotionEffect slow = new PotionEffect( PotionEffectType.SLOW, 60, 2 );
                        ((Monster) event.getEntity()).addPotionEffect( slow );
                        wizardPlayer.sendMsgWithCooldown( "Slow Bolt",
                                ChatColor.AQUA + "You have invoked Slow Bolt with your Wizard Crossbow!",
                                5 );
                    } else if ( customName.equals( "Poison Bolt" ) ) {
                        PotionEffect poison = new PotionEffect( PotionEffectType.POISON, 60, 4 );
                        ((Monster) event.getEntity()).addPotionEffect( poison );
                        wizardPlayer.sendMsgWithCooldown( "Poison Bolt",
                                ChatColor.AQUA + "You have invoked Poison Bolt with your Wizard Crossbow!",
                                5 );
                    }               }
            }

            // ***** Trident ***** //
            if ( damager instanceof Trident ) {
                Trident trident = (Trident) damager;

                if ( trident.getCustomName() == null ) return;
                Entity victim = event.getEntity();
                if ( trident.getCustomName().equals( "Fiery Pitchfork" ) ) {
                    if ( !wizardPlayer.spendWizardPower( WizardTrident.getModeCost( "Fiery Pitchfork" ) , null ) ) return;
                    victim.setFireTicks( 80 );
                    SpecialEffects.flamesEffect( victim.getLocation().add( -.5, 0, -.5 ) );
                    wizardPlayer.sendMsgWithCooldown( "Wizard Trident (Fiery Pitchfork)",
                            ChatColor.AQUA + "You have invoked Fiery Pitchfork using your Wizard Trident!", 10 );
                } else if ( trident.getCustomName().equals( "Monster Slayer" ) ) {
                    if ( ! ( victim instanceof Monster ) ) return;
                    if ( !wizardPlayer.spendWizardPower( WizardTrident.getModeCost( "Monster Slayer" ), null ) ) return;
                    event.setDamage( event.getDamage() * 2 );
                    SpecialEffects.magicSpiral( victim.getLocation() );
                    wizardPlayer.sendMsgWithCooldown( "Wizard Trident (Monstery Slayer)",
                            ChatColor.AQUA + "You have invoked Monstery Slayer using your Wizard Trident!", 10 );
                } else if ( trident.getCustomName().equals( "Hunting Spear" ) ) {
                    if ( ! ( victim instanceof Animals ) ) return;
                    if ( !wizardPlayer.spendWizardPower( WizardTrident.getModeCost( "Hunting Spear" ), null ) ) return;
                    event.setDamage( event.getDamage() * 2 );
                    SpecialEffects.portalCollapse( victim.getLocation() );
                    wizardPlayer.sendMsgWithCooldown( "Wizard Trident (Hunting Spear)",
                            ChatColor.AQUA + "You have invoked Hunting Spear using your Wizard Trident!", 10 );
                }
            }

        } else if ( event.getDamager() instanceof Player ){
            Player p = (Player) event.getDamager();
            ItemStack main = p.getInventory().getItemInMainHand();
            if ( main != null && main.getType().toString().contains( "SWORD" ) && main.getItemMeta() != null ) {
                if ( main.getItemMeta().getLore() == null ) return;
                List<String> lore = main.getItemMeta().getLore();
                if ( !lore.get(0).equals( "Wizard Sword" ) ) return;
                if ( lore.get(1) == null || lore.get(1).equals( "Mode: Normal" ) || lore.get(1).equals( "Mode: Whirlwind Slash" ) ) return;
                WizardPlayer wizardPlayer = wizardry.getWizardPlayer( p.getUniqueId() );
                String mode = lore.get(1).substring( 6 );
                if ( mode.equals( "Lifestealer" ) ) {
                    if ( !wizardPlayer.spendWizardPower( WizardSword.getModeCost( "Lifestealer" ), "Wizard Sword (Lifestealer)" ) ) return;
                    PotionEffect effect = new PotionEffect( PotionEffectType.REGENERATION, 30, 2 );
                    p.addPotionEffect( effect );

                } else if ( mode.equals( "Moonblade") && p.getWorld().getTime() > 13000 ) {
                    event.setDamage( event.getDamage() + 4 );
                }

            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event ) {
        Entity entity = event.getEntity();
        if ( entity != null ) {
            // We only care about Player projectiles
            if ( !( ((Projectile) entity).getShooter() instanceof Player ) ) return;
            Player p = (Player) ((Projectile) entity).getShooter();
            WizardPlayer wizardPlayer = Wizardry.getWizardry().getWizardPlayer( p.getUniqueId() );

            if ( entity instanceof SmallFireball && entity.getCustomName().equals( "Incinerate Projectile" ) ) {
                if ( event.getHitEntity() != null && event.getHitEntity() instanceof LivingEntity ) {
                    event.getHitEntity().setFireTicks( 60 );
                    ((LivingEntity) event.getHitEntity()).damage( 15 );
                }
                entity.getWorld().createExplosion( entity.getLocation(), 0, false );
            } else if ( entity instanceof Arrow ) {
                String customName = entity.getCustomName();
                if ( customName != null ) {
                    if ( !( customName.equals( "Bow of Teletransference" ) )
                        && !( customName.equals( "Bane of Darkness" ) ) ) return;

                    if ( event.getHitBlock() == null ) return;
                    try {
                        WeaponPattern magicBow = wizardry.getWeaponByLore( "Wizard Bow" );
                        WeaponFunction weaponFunction = magicBow.getWeaponFunction();
                        weaponFunction.weapon = p.getInventory().getItemInMainHand();
                        weaponFunction.mode = customName;
                        weaponFunction.projectileHitEvent = event;
                        weaponFunction.player = p;
                        weaponFunction.wizardPlayer = wizardPlayer;
                        weaponFunction.call();
                    } catch ( Exception ex ) {
                        ex.printStackTrace( );
                    }
                }

            }
        }
    }
}
