package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

import java.util.ArrayList;

public class TerracottaTurner extends MagicSpell {

    public TerracottaTurner() {
        spellName = "Terracotta Turner";
        reagents = new ArrayList<String>();
        reagents.add( "TERRACOTTA" );
        cost = 0;

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                String type = clickedBlock.getType().toString();
                if ( type.contains( "GLAZED_TERRACOTTA" ) ) {
                    WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                    String state = clickedBlock.getState().getBlockData().getAsString();
                    if (state.contains("facing=west")) {
                        state = state.replace( "facing=west", "facing=north");
                    } else if (state.contains("facing=north")) {
                        state = state.replace( "facing=north", "facing=east");
                    } else if (state.contains("facing=east")) {
                        state = state.replace( "facing=east", "facing=south");
                    } else if (state.contains("facing=south")) {
                        state = state.replace( "facing=south", "facing=west");
                    }

                    clickedBlock.setBlockData( Bukkit.getServer().createBlockData( state ) );
                    if ( wizardPlayer.checkMsgCooldown( spellName ) == false ) {
                        player.sendMessage(ChatColor.DARK_PURPLE + "You have invoked " + spellName + "!");
                        wizardPlayer.addMsgCooldown( spellName, 30 );
                    }

                }
            }
        };

    }
}
