package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.Targeter;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;

import java.util.ArrayList;
import java.util.List;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

/**
 * TOLL OF MADNESS
 * A spell that makes a monster attack a nearby monster.
 */

public class TollOfMadness extends MagicSpell {

    public TollOfMadness() {
        spellName = "Toll of Madness";
        reagents = new ArrayList<String>();
        reagents.add( "PHANTOM_MEMBRANE" );
        cost = .01;

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                org.bukkit.entity.Entity target = Targeter.getTargetEntity( player );
                if ( target != null ) {
                    if ( target instanceof Monster) {
                        List<Entity> entities = target.getNearbyEntities( 15D, 10D, 15D );
                        ArrayList<Entity> monsters = new ArrayList<Entity>();
                        for ( Entity e : entities) {
                            if ( e instanceof Monster ) {
                                monsters.add( e );
                            }
                        }
                        int index = (int)Math.floor( Math.random() * monsters.size() );
                        Entity newTarget = monsters.get( index );
                        if ( newTarget != null && wizardPlayer.spendWizardPower( cost ) ) {
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "Madness takes its toll!");
                            ((Monster) target).setTarget( (LivingEntity) newTarget );
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_PURPLE + "No target found for Toll of Madness!");
                }



            }
        };


    }
}