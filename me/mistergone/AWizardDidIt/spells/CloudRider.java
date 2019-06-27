package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.baseClasses.MagicSpell;
import me.mistergone.AWizardDidIt.baseClasses.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

/**
 * CLOUD RIDER
 * A spell that sends the wizard high into the air, to fly off into the clouds!
 */

public class CloudRider extends MagicSpell {

    public CloudRider() {
        spellName = "Cloud Rider";
        cost = 25;
        int slowFallCost = 5;
        reagents = new ArrayList<String>();
        reagents.add("FEATHER");

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer(player.getUniqueId());
                Block feet = player.getWorld().getBlockAt(player.getLocation());
                Block head = player.getWorld().getBlockAt(player.getLocation()).getRelative(BlockFace.UP);
                Boolean isFloating = feet.getType() == Material.WATER && head.getType() == Material.AIR;

                if ((player.isOnGround() || isFloating)) {
                    if ( !wizardPlayer.spendWizardPower( cost, spellName ) ) return;

                    wizardPlayer.addSpell(spellName);

                    if (wizardPlayer.checkMsgCooldown( spellName ) == false) {
                        player.sendMessage( ChatColor.AQUA + "You have invoked Cloud Rider!" );
                        wizardPlayer.addMsgCooldown(spellName, 30 );
                    }

                    player.playSound(player.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE, 1.2F, 1.2F);
                    Location loc = player.getLocation();
                    Vector v = player.getVelocity();
                    int xFactor = 1;
                    int zFactor = 1;
                    double angle = Math.round(player.getLocation().getYaw());

                    double speed = 2;
                    double pitch = 45;
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
                    if (player.isSprinting()) {
                        sprinting = 1.5;
                    }

                    v.setX(Math.sin(angle) * speed * sprinting * xFactor);
                    v.setY(Math.sin(Math.toRadians(pitch)) * (sprinting + 0.5) * speed);
                    v.setZ(Math.cos(angle) * speed * sprinting * zFactor);

                    player.setVelocity(v);
                    player.sendMessage(ChatColor.DARK_AQUA + "Swing your wand again to begin gliding!");

                } else if (!player.isGliding() && !player.isSwimming() && wizardPlayer.getSpells().contains(spellName)) {
                    player.setGliding(true);
                    player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.2F, .8F);

                    // TODO - Make this code reusable, duh
                    Location loc = player.getLocation();
                    Vector v = player.getVelocity();
                    int xFactor = 1;
                    int zFactor = 1;
                    double angle = Math.round(player.getLocation().getYaw());

                    double speed = 3;
                    double pitch = 15;
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
                    if (player.isSprinting()) {
                        sprinting = 1.5;
                    }

                    v.setX(Math.sin(angle) * speed * sprinting * xFactor);
                    v.setY(Math.sin(Math.toRadians(pitch)) * (sprinting + 0.5) * speed);
                    v.setZ(Math.cos(angle) * speed * sprinting * zFactor);

                    player.setVelocity(v);

                    wizardPlayer.removeSpell("Cloud Rider");
                    wizardPlayer.addSpell("Cloud Rider (Gliding)");

                    player.sendMessage(ChatColor.AQUA + "You are now gliding!");

                } else if (!player.isOnGround() && !player.isGliding() && !wizardPlayer.getSpells().contains(spellName)
                        && !wizardPlayer.getSpells().contains(spellName + " (Gliding)") ) {
                    if ( !wizardPlayer.spendWizardPower( slowFallCost, spellName ) ) return;
                    PotionEffect slowFall = new PotionEffect(PotionEffectType.SLOW_FALLING, 200, 1);
                    player.addPotionEffect(slowFall);

                    player.sendMessage(ChatColor.AQUA + "You have invoked " + spellName + " to slow your fall!");
                }
            }
        };
    }
}
