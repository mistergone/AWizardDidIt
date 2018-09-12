package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class CloudRider extends MagicSpell {

    public CloudRider() {
        spellName = "Cloud Rider";
        cost = .01;
        reagent = Material.FEATHER;

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer(player.getUniqueId());
                double wizardPower = wizardPlayer.getWizardBar().getProgress();
                Bukkit.broadcastMessage(String.valueOf(wizardPower));

                if (player.isOnGround() && wizardPlayer.spendWizardPower( cost ) ) {
                    wizardPlayer.addSpell(spellName);

                    if (wizardPlayer.checkMsgCooldown(spellName) == false) {
                        player.sendMessage(ChatColor.AQUA + "You have invoked Cloud Rider!");
                        wizardPlayer.addMsgCooldown(spellName, 20);
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
                    player.sendMessage("Swing your wand again to begin gliding!");
                    wizardPlayer.showWizardBar();

                } else if (!player.isGliding() && !player.isSwimming() && wizardPlayer.getSpells().contains( spellName )) {
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

                    wizardPlayer.removeSpell( "Cloud Rider" );
                    wizardPlayer.addSpell( "Cloud Rider (Gliding)");

                }
            }
        };
    }
}
