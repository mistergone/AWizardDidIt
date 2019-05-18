package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class AlfsActionArrow extends MagicSpell {

    public AlfsActionArrow() {
        spellName = "Alf's Action Arrow";
        cost = 2;
        reagents = new ArrayList<String>();
        reagents.add("ARROW");

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer(player.getUniqueId());
                int reagentCount = reagent.getAmount();
                if ( wizardPlayer.getWizardPower() >= cost || reagentCount > 1 ) {
                    if ( reagentCount > 1 ) {
                        reagent.setAmount( reagentCount - 1 );
                    } else {
                        wizardPlayer.spendWizardPower( cost );
                    }

                    if ( wizardPlayer.checkSpell( spellName ) ) {
                        player.sendMessage( ChatColor.RED + spellName + " is not ready yet!" );
                        return;
                    } else {
                        wizardPlayer.setSpellTimer( spellName, 20 );
                    }

                    if ( wizardPlayer.checkMsgCooldown( spellName ) == false ) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "You have invoked " + spellName + "!");
                        wizardPlayer.addMsgCooldown( spellName, 5 );
                    }

                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, .6F, .1F);
                    Vector v = new Vector();
                    int xFactor = 1;
                    int zFactor = 1;
                    double angle = Math.round(player.getLocation().getYaw());

                    double speed = 2;
                    double pitch = -1 * Math.round(player.getLocation().getPitch());
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

                    Arrow arrow = player.launchProjectile(Arrow.class);
                    arrow.setColor( Color.FUCHSIA );
                    arrow.setCustomName("Alf's Action Arrow Projectile");
                    arrow.setShooter((ProjectileSource) player);
                    arrow.setVelocity(v);
                } else {
                    wizardPlayer.spendWizardPower( cost );
                }

            }
        };
    }

}
