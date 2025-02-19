package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.baseClasses.MagicSpell;
import me.mistergone.AWizardDidIt.baseClasses.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

/**
 * MIGHTY LEAP
 * A spell that flings the wizard into the air and protects them from the fall... probably.
 */

public class MightyLeap extends MagicSpell {

    public MightyLeap() {
        spellName = "Mighty Leap";
        reagents = new ArrayList<String>();
        reagents.add( "SLIME_BALL" );
        cost = 25;

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );

                if ( player.isOnGround() ) {
                    if ( !wizardPlayer.spendWizardPower( cost, spellName ) ) return;
                    wizardPlayer.addSpell( spellName );

                    if ( wizardPlayer.checkMsgCooldown( "Magic Leap" ) == false ) {
                        wizardPlayer.spellAlert( spellName, ChatColor.DARK_GREEN + "Safety is not guaranteed!" );
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
                    // Multiply pitch by -1 because it makes it easier to understand :p
                    double pitch = Math.round( player.getLocation().getPitch() ) * -1;
                    // if the player is pitched at > -15 degrees, make it at least +30 degrees
                    if ( pitch > -15 ) {
                        pitch = (double)Math.min( pitch, 30 );
                    // If the player is looking down, just launch them forward a little.
                    } else if ( pitch < -60 ) {
                        speed = .5;
                        pitch = 5;
                    }

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

                    wizardPlayer.setSpellTimer( spellName, 200 );
                }
            }
        };
    }
}
