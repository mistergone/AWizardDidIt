package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.SmallFireball;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

/**
 * INCINERATE
 * A spell that causes the target (Monster) to burst into flames
 */
public class Incinerate extends MagicSpell {
    public Incinerate() {
        spellName = "Incinerate";
        cost = 20;
        reagents = new ArrayList<String>();
        reagents.add( "FIREWORK_STAR" );

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
//                Entity ent = Targeter.getTargetEntity( player );
//                SmallFireball fireball = (SmallFireball)player.getWorld().spawnEntity( player.getEyeLocation(), EntityType.SMALL_FIREBALL );
//                fireball.setDirection( player.getLocation().getDirection() );
                if ( wizardPlayer.spendWizardPower( cost ) ) {
                    Vector v = new Vector();
                    int xFactor = 1;
                    int zFactor = 1;
                    double angle = Math.round(player.getLocation().getYaw());

                    double speed = 2.5;
                    double pitch = -1 * Math.round(player.getLocation().getPitch() );
                    if (angle >= 270) {
                        angle = Math.toRadians(Math.abs(angle - 360));
                    } else if (angle >= 180) {
                        zFactor = -1;
                        angle = Math.toRadians(angle - 180);
                    } else if (angle > 90) {
                        xFactor = -1;
                        zFactor = -1;
                        angle = Math.toRadians(Math.abs(angle - 180));
                    } else {
                        xFactor = -1;
                        angle = Math.toRadians(angle);
                    }

                    v.setX(Math.sin(angle) * speed * xFactor);
                    v.setY(Math.sin(Math.toRadians(pitch)) * speed);
                    v.setZ(Math.cos(angle) * speed * zFactor);

                    SmallFireball fireball = (SmallFireball)player.launchProjectile( SmallFireball.class );
                    fireball.setIsIncendiary( false );
                    fireball.setYield( 0 );
                    fireball.setCustomName( "Incinerate Projectile");
                    fireball.setShooter( (ProjectileSource)player );
                    fireball.setVelocity( v );

                } else {
                    if ( !wizardPlayer.checkMsgCooldown( spellName + "OOM") ) {
                        player.sendMessage( ChatColor.DARK_RED + "You do not have enough Wizard Power to invoke " + spellName );
                        wizardPlayer.addMsgCooldown(spellName + "OOM", 5 );
                    }
                }




//                if ( ent instanceof Monster && wizardPlayer.spendWizardPower( cost ) ) {
//                    ent.setFireTicks( 300 );
//                    ent.getWorld().createExplosion( ent.getLocation(), 0, false );
//                    ((Monster) ent).setTarget( player );
//                    ((Monster) ent).damage( 6 , player );
//                }



            }
        };
    }
}
