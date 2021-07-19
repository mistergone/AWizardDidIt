package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.baseClasses.MagicSpell;
import me.mistergone.AWizardDidIt.baseClasses.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class XomirsQuiver extends MagicSpell {

    public XomirsQuiver() {
        spellName = "Xomir's Quiver";
        cost = 100;
        reagents = new ArrayList<String>();
        reagents.add( "CROSSBOW" );
        reagents.add( "BOW" );

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                if ( !wizardPlayer.spendWizardPower( cost, spellName ) ) return;

                if ( wizardPlayer.checkMsgCooldown( spellName ) == false ) {
                    player.sendMessage(ChatColor.AQUA + "You have invoked " + spellName + "!");
                    wizardPlayer.addMsgCooldown( spellName, 10 );
                }
                ItemStack arrows = new ItemStack( Material.ARROW );
                arrows.setAmount( 16 );
                player.getWorld().dropItem( player.getLocation(), arrows );
            }
        };
    }

}
