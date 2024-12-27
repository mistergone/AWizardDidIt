package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.AWizardDidIt;
import me.mistergone.AWizardDidIt.baseClasses.MagicSpell;
import me.mistergone.AWizardDidIt.baseClasses.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WandHelper;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

/**
 * CLOUD RIDER
 * A spell that sends the wizard high into the air, to fly off into the clouds!
 */

public class CloudRider extends MagicSpell {
    String boatSpellName = "Wave Rider";
    String railSpellName = "Rail Rider";

    public CloudRider() {
        spellName = "Cloud Rider";
        cost = 100;
        int slowFallCost = 5;
        int waveRiderCost = 10;

        reagents = new ArrayList<String>();
        reagents.add("FEATHER");

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer(player.getUniqueId());
                Block feet = player.getWorld().getBlockAt(player.getLocation());
                Block head = player.getWorld().getBlockAt(player.getLocation()).getRelative(BlockFace.UP);
                Boolean isFloating = feet.getType() == Material.WATER && head.getType() == Material.AIR;
                Boolean isInVehicle = player.isInsideVehicle();

                if ((player.isOnGround() || isFloating) && !isInVehicle ) {
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
                    double pitch = 60;
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

                } else if (!player.isGliding() && !player.isSwimming() && !isInVehicle && wizardPlayer.getSpells().contains(spellName)) {
                    wizardPlayer.removeSpell("Cloud Rider");
                    wizardPlayer.addSpell("Cloud Rider (Gliding)");
                    player.setGliding( true );
                    player.playSound(player.getLocation(), Sound.ENTITY_BREEZE_WIND_BURST, 1.2F, .8F);

                    doStartingBurst( player );

                    player.sendMessage(ChatColor.AQUA + "You are now gliding!");

                } else if (!player.isOnGround() && !player.isGliding() && !isInVehicle && !wizardPlayer.getSpells().contains(spellName)
                        && !wizardPlayer.getSpells().contains(spellName + " (Gliding)") ) {
                    if ( !wizardPlayer.spendWizardPower( slowFallCost, spellName ) ) return;
                    PotionEffect slowFall = new PotionEffect(PotionEffectType.SLOW_FALLING, 200, 1);
                    player.addPotionEffect(slowFall);

                    player.sendMessage(ChatColor.AQUA + "You have invoked " + spellName + " to slow your fall!");
                } else if ( isInVehicle ) {
                    Entity vehicle = player.getVehicle();
                    if ( vehicle instanceof Boat ) {
                        if ( wizardPlayer.checkSpell( boatSpellName ) ) {
                            wizardPlayer.addSpell( boatSpellName + " - Jump" );
                        } else  if ( wizardPlayer.spendWizardPower( cost, spellName )) {
                            player.sendMessage(ChatColor.AQUA + "You have invoked " + boatSpellName +"!");
                            doBoating( (Boat)vehicle, wizardPlayer );
                        }

                    } else if ( vehicle instanceof Minecart ) {
                        Location loc = player.getLocation();
                        Vector v = player.getVelocity();
                        int xFactor = 1;
                        int zFactor = 1;
                        double angle = Math.round(player.getLocation().getYaw());

                        double speed = 10;
                        double pitch = 0;
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
                        v.setY(0);
                        v.setZ(Math.cos(angle) * speed * zFactor);

                        player.setVelocity(v);

                        player.sendMessage(ChatColor.AQUA + "You have invoked " + railSpellName +"!");
                    }
                }
            }
        };
    }

    private void doBoating( Boat boat, WizardPlayer wizardPlayer ) {
        Player player = wizardPlayer.getPlayer();
        final AtomicInteger time = new AtomicInteger();
        AWizardDidIt plugin = (AWizardDidIt)Bukkit.getServer().getPluginManager().getPlugin("AWizardDidIt");
        player.setAllowFlight( true );
        wizardPlayer.addSpell( boatSpellName );
        time.set( 0 );

        new BukkitRunnable(){
            @Override
            public void run() {
                if ( !boat.getPassengers().contains( player )
                        || !WandHelper.isActuallyAWand( player.getInventory().getItemInMainHand() ) ) {
                    player.sendMessage(ChatColor.AQUA + boatSpellName + ChatColor.YELLOW + " has ended.");
                    player.setAllowFlight( false );
                    wizardPlayer.removeSpell( boatSpellName );
                    wizardPlayer.removeSpell( boatSpellName + " - Jump" );
                    cancel();
                    return;
                }

                Location loc = boat.getLocation();
                Vector v = boat.getVelocity();
                double angle = Math.round(boat.getLocation().getYaw());

                int xFactor = 1;
                int zFactor = 1;
                double speed = 1;
                Block b = boat.getLocation().getBlock();
                if ( b.getType() != Material.WATER ) {
                    speed = .5;
                }


                // Determine Y
                if ( wizardPlayer.checkSpell( boatSpellName + " - Jump" ) ) {
                    time.getAndIncrement();
                    double y = Math.sin( Math.toRadians( 60 ) ) - ( .1 * time.intValue() );

                    if ( y < 0
                            && ( b.getType() == Material.WATER || b.getRelative( BlockFace.DOWN ).getType() != Material.AIR ) ) {
                        time.set( 0 );
                        y = 0;
                        wizardPlayer.removeSpell( boatSpellName + " - Jump" );
                    }
                    y = Math.max( y, -1 );

                    v.setY( y );

                } else {
                    if ( b.getType() == Material.AIR ) {
                        time.set( 8 );
                        wizardPlayer.addSpell( boatSpellName + " - Jump" );
                    }
                    v.setY(0);
                }

                // Determine X and Z
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
                v.setZ(Math.cos(angle) * speed * zFactor);

                boat.setVelocity(v);

            }
        }.runTaskTimer( plugin, 0, 1 );

    }

    private void doStartingBurst( Player player ) {
        final AtomicInteger time = new AtomicInteger();
        AWizardDidIt plugin = (AWizardDidIt)Bukkit.getServer().getPluginManager().getPlugin("AWizardDidIt");
        time.set( 0 );

        new BukkitRunnable(){
            @Override
            public void run() {
                time.incrementAndGet();
                glidingBurst( player );
                if ( time.get() > 2 ) {
                    cancel();
                }
            }
        }.runTaskTimer( plugin, 0, 3 );
    }

    private void glidingBurst( Player player ) {
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
    }
}
