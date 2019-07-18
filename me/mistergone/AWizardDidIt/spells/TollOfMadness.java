package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.baseClasses.MagicSpell;
import me.mistergone.AWizardDidIt.baseClasses.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.MobManager;
import me.mistergone.AWizardDidIt.helpers.Targeter;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.ChatColor;
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
        cost = 10;

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                org.bukkit.entity.Entity target = Targeter.getTargetEntity( player );
                if ( target != null ) {
                    if ( MobManager.isMonster( target ) ) {
                        List<Entity> entities = target.getNearbyEntities( 15D, 10D, 15D );
                        ArrayList<Entity> monsters = new ArrayList<Entity>();
                        for ( Entity e : entities) {
                            if ( MobManager.isMonster( e ) ) {
                                monsters.add( e );
                            }
                        }
                        int index = (int)Math.floor( Math.random() * monsters.size() );
                        Entity newTarget = monsters.get( index );
                        if ( newTarget != null ) {
                            if ( !wizardPlayer.spendWizardPower( cost, spellName ) ) return;
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