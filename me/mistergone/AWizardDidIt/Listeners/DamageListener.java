package me.mistergone.AWizardDidIt.Listeners;

import me.mistergone.AWizardDidIt.Wizardry;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

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
                if ( wizardPlayer.spendWizardPower( damageCost ) ) {
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
            if (event.getDamager() instanceof Arrow) {
                if ( event.getDamager().getCustomName() != null && event.getDamager().getCustomName().equals( "Alf's Action Arrow Projectile" ) ) {
                    if ( event.getEntity() instanceof Monster) {
                        double damage =  Math.floor( ( Math.random() * 6 ) + 5 );
                        event.setDamage( damage );
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event ) {
        Entity entity = event.getEntity();
        if ( entity != null ) {
            if ( entity instanceof SmallFireball && entity.getCustomName().equals( "Incinerate Projectile" ) ) {
                if ( event.getHitEntity() != null && event.getHitEntity() instanceof LivingEntity ) {
                    event.getHitEntity().setFireTicks( 60 );
                    ((LivingEntity) event.getHitEntity()).damage( 15 );
                } else if ( event.getHitBlock() != null ) {

                }
                entity.getWorld().createExplosion( entity.getLocation(), 0, false );
            }
        }
    }
}
