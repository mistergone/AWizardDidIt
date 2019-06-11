package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

/**
 * HOLY DIVER
 * A spell that propels the caster through the water and grants a bit of waterbreathing.
 */
public class HolyDiver extends MagicSpell {
    public HolyDiver() {
        spellName = "Holy Diver";
        reagents = new ArrayList<String>();
        reagents.add( "TROPICAL_FISH" );
        cost = 10;

        spellFunction = new SpellFunction() {
          @Override
          public void run() {
              WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
              Location loc = player.getLocation();
              Block head = player.getWorld().getBlockAt( loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ() );
              Boolean underwater = head.getType() == Material.WATER;
              if ( ( player.isSwimming() || underwater ) && wizardPlayer.spendWizardPower( cost ) ) {

                  if ( wizardPlayer.checkMsgCooldown( spellName ) == false ) {
                      player.sendMessage(ChatColor.BLUE + "You have invoked Holy Diver! You've been down too long in the midnight sea!");
                      wizardPlayer.addMsgCooldown( spellName, 20 );
                  }

                  // First, give the player a boost of speed
                  Vector v = player.getVelocity();
                  int xFactor = 1;
                  int zFactor = 1;
                  double angle = Math.round(player.getLocation().getYaw());

                  double speed = 0.75;
                  double pitch = Math.toRadians( -1 * Math.round( player.getLocation().getPitch() ) );
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
                  v.setY(Math.sin(pitch) * speed);
                  v.setZ(Math.cos(angle) * speed * zFactor);

                  player.setVelocity(v);

                  // Second, give the player a little Dolphin Power
                  PotionEffect grace = new PotionEffect( PotionEffectType.DOLPHINS_GRACE, 100, 1 );
                  PotionEffect fishy = new PotionEffect( PotionEffectType.WATER_BREATHING, 100, 1 );
                  player.addPotionEffect( grace );
                  player.addPotionEffect( fishy );

                  // Third, refill some bubbles
                  int gasp = player.getMaximumAir() - player.getRemainingAir();
                  if ( wizardPlayer.spendWizardPower( gasp / 10000 ) ) {
                      player.setRemainingAir( player.getMaximumAir() );
                  }
              } else {
                  if ( !wizardPlayer.checkMsgCooldown( spellName + "OOM") ) {
                      player.sendMessage( ChatColor.DARK_RED + "You do not have enough Wizard Power to invoke " + spellName );
                      wizardPlayer.addMsgCooldown(spellName + "OOM", 5 );
                  }
                  return;
              }
          }
        };
    }

}
