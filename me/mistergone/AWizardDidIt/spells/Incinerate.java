package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.Targeter;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

/**
 * INCINERATE
 * A spell that causes the target (Monster) to burst into flames
 */
public class Incinerate extends MagicSpell {
    public Incinerate() {
        spellName = "Incinerate";
        cost = .005;
        reagents = new ArrayList<String>();
        reagents.add( "FIREWORK_STAR" );

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                Entity ent = Targeter.getTargetEntity( player );
                if ( ent instanceof Monster && wizardPlayer.spendWizardPower( cost ) ) {
                    ent.setFireTicks( 300 );
                    ent.getWorld().createExplosion( ent.getLocation(), 0, false );
                    ((Monster) ent).setTarget( player );
                    ((Monster) ent).damage( 6 , player );
                }



            }
        };
    }
}
