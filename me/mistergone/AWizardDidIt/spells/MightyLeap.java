package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.Targeter;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

/**
 * MIGHTY LEAP
 * A spell that flings the wizard into the air and protects them from the fall... probably.
 */

public class MightyLeap extends MagicSpell {

    public MightyLeap() {
        spellName = "Mighty Leap";
        reagent = Material.SLIME_BALL;
        cost = .001;
        double glidingCost = .01;

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );

                if ( player.isOnGround() && wizardPlayer.spendWizardPower( cost ) ) {
                    wizardPlayer.addSpell( spellName );

                    if ( wizardPlayer.checkMsgCooldown( "Magic Leap" ) == false ) {
                        player.sendMessage(ChatColor.DARK_GREEN + "You have invoked Mighty Leap! Safety is not guaranteed!");
                        wizardPlayer.addMsgCooldown( "Magic Leap", 20 );
                    }

                    player.playSound(player.getLocation(), Sound.ENTITY_SLIME_JUMP, .6F, .2F);
                    Location loc = player.getLocation();
                    Vector v = player.getVelocity();
                    int xFactor = 1;
                    int zFactor = 1;
                    double angle = Math.round(player.getLocation().getYaw());

                    // Set speed based on slimeball count
                    ItemStack slimeBalls = player.getInventory().getItemInOffHand();
                    double speed = 0;
                    if ( slimeBalls != null && slimeBalls.getType() == Material.SLIME_BALL ) {
                        speed = .75 + (double)slimeBalls.getAmount() / 64 * 2.25;
                    }
                    // We want the angle on the Y part of the vector to be at least 30 degrees
                    double pitch = Math.max( (double)Math.abs( Math.round(player.getLocation().getPitch() ) ), 30);
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

                    double sprinting = 1;
                    if ( player.isSprinting() ) {
                        sprinting = 1.5;
                    }



                    v.setX(Math.sin(angle) * speed * sprinting * xFactor);
                    v.setY(Math.sin(Math.toRadians(pitch)) * (sprinting + 0.5 ) * speed);
                    v.setZ(Math.cos(angle) * speed * sprinting * zFactor);

                    player.setVelocity(v);

                    wizardPlayer.setMightyLeapTimer();
                }
            }
        };
    }
}
