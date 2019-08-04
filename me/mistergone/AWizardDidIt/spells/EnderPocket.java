package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.baseClasses.MagicSpell;
import me.mistergone.AWizardDidIt.baseClasses.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class EnderPocket extends MagicSpell {

    public EnderPocket() {
        spellName = "Ender Pocket";
        reagents = new ArrayList<String>();
        reagents.add( "ENDER_PEARL" );
        cost = 10;

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                if ( !wizardPlayer.spendWizardPower( cost, spellName ) ) return;

                player.openInventory( player.getEnderChest() );
                wizardPlayer.sendMsgWithCooldown( spellName,
                        ChatColor.GREEN + "You have invoked " + spellName + "!",
                        10 );

            }
        };

    }
}
