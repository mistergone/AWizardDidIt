package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.MagicPattern;
import me.mistergone.AWizardDidIt.helpers.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.ChatColor;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class FontOfPower extends MagicPattern {
    public FontOfPower() {
        patternName = "Font of Power";

        patterns =  new ArrayList<String[]>();
        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                    "GLOWSTONE_DUST", "NONE", "GLOWSTONE_DUST",
                    "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );

        patternFunction = new PatternFunction() {
          @Override
          public void run() {
              WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
              if ( wizardPlayer.getWizardPower() < 1000 ) {
                  wizardPlayer.gainWizardPower( 100 );
                  player.sendMessage( ChatColor.GOLD + "You have invoked Font of Power, regaining 100 Wizard Power!");
                  magicChest.clearPattern();
              } else {
                  player.sendMessage( ChatColor.GOLD + "You have invoked Font of Power, but your Wizard Power is full!");
              }


          }
        };

    }
}
