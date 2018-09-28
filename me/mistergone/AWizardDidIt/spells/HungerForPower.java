package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.ExpManager;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

/**
 *  HUNGER FOR POWER
 *  A spell that lets the caster trade Exp or Life for Wizard Power
 */

public class HungerForPower extends MagicSpell {
    public HungerForPower() {

        spellName = "Hunger for Power";
        cost = 0;
        reagents = new ArrayList<String>();
        reagents.add( "ROTTEN_FLESH" );
        reagents.add( "BONE" );

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer(player.getUniqueId());
                if ( reagent.getType() == Material.BONE ) {
                    if ( !wizardPlayer.checkSpell( spellName + "(Exp)" ) ) {
                        player.sendMessage(ChatColor.YELLOW + "You have invoked " + spellName
                                + "! If you wish to trade 50 Experience Points for 50 Wizard Points, swing your wand again!");
                        wizardPlayer.addSpell( spellName + "(Exp)" );
                        wizardPlayer.setSpellTimer( spellName + "(Exp)", 100 );
                    } else {
                        if ( ExpManager.spendExp( player, 50 ) ) {
                            wizardPlayer.gainWizardPower( 50 );
                        } else {
                            player.sendMessage( ChatColor.RED + "You do not have sufficient Experience Points!");
                        }
                        wizardPlayer.removeSpell( spellName + "(Exp)");
                    }
                } else if ( reagent.getType() == Material.ROTTEN_FLESH ) {
                    if ( !wizardPlayer.checkSpell( spellName + "(Health)" ) ) {
                        player.sendMessage(ChatColor.YELLOW + "You have invoked " + spellName
                                + "! If you wish to trade 1 Heart of Health for 25 Wizard Points, swing your wand again!");
                        wizardPlayer.addSpell( spellName + "(Health)" );
                        wizardPlayer.setSpellTimer( spellName + "(Health)", 100 );
                    } else {
                        if ( player.getHealth() > 2 ) {
                            player.damage( 2 );
                            wizardPlayer.gainWizardPower( 25 );
                        } else {
                            player.sendMessage( ChatColor.RED + "You do not have sufficient Health!");
                        }
                        wizardPlayer.removeSpell( spellName + "(Health)");
                    }

                }

            }
        };
    }

}
