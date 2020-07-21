package me.mistergone.AWizardDidIt.spells;
import me.mistergone.AWizardDidIt.baseClasses.MagicSpell;
import me.mistergone.AWizardDidIt.baseClasses.SpellFunction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BringTheDawn extends MagicSpell {
    public BringTheDawn() {
        spellName = "Bring the Dawn";
        reagents = new ArrayList<String>();
        reagents.add( "SUNFLOWER" );
        cost = 250;

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                if ( wizardPlayer.checkSpellCooldown( spellName ) ) {
                    player.sendMessage(ChatColor.YELLOW + spellName + " is still on cooldown." );
                    return;
                }
                if ( player.getWorld().getTime() < 12542 ) {
                    player.sendMessage(ChatColor.YELLOW + "It is too early to use this spell!" );
                    return;
                }
                if ( wizardPlayer.spendWizardPower( cost, spellName ) ) {
                    List<Player> playerList = player.getWorld().getPlayers();
                    int playerCount = playerList.size() - 1;
                    int sleeping = 0;
                    for ( Player p : playerList ) {
                        if ( p.isSleeping() ) {
                            sleeping++;
                        }
                    }
                    if ( playerCount == 0 || (double)sleeping / (double)playerCount > .5 ) {
                        player.getWorld().setTime( 0 );
                        wizardPlayer.addSpellCooldown( spellName, 24000);
                    } else {
                        player.sendMessage( ChatColor.YELLOW + "Not enough players are in beds to invoke " + spellName + "!" );
                    }
                }

            }
        };
    }
}
