package me.mistergone.AWizardDidIt.spells;
import me.mistergone.AWizardDidIt.AWizardDidIt;
import me.mistergone.AWizardDidIt.baseClasses.MagicSpell;
import me.mistergone.AWizardDidIt.baseClasses.SpellFunction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RainbowInTheDark extends MagicSpell {
    public RainbowInTheDark() {
        spellName = "Rainbow in the Dark";
        reagents = new ArrayList<String>();
        reagents.add( "SUNFLOWER" );
        cost = 300;

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                if ( player.getWorld().getTime() < 13000 ) {
                    player.sendMessage(ChatColor.YELLOW + "It is too early to use this spell!" );
                    return;
                }

                List<Player> playerList = player.getWorld().getPlayers();
                int playerCount = playerList.size();
                int sleeping = 0;
                for ( Player p : playerList ) {
                    if ( p.isSleeping() ) {
                        sleeping++;
                    }
                }

                int s = 1;
                int c = 5;

                if ( ( (double) sleeping + 1 ) / (double) playerCount >= .25 ){
                    if ( wizardPlayer.spendWizardPower( cost, spellName ) ) {
                        cryOutForMagic( player );
                        Bukkit.broadcastMessage( ChatColor.YELLOW + "There's no sign of the morning coming! There's no sign of the day!" );
                    }
                } else {
                    player.sendMessage( ChatColor.YELLOW + "There are not enough players in beds to invoke " + spellName + "!" );
                }
            }
        };
    }

    private void cryOutForMagic( Player p ) {
        AWizardDidIt plugin = (AWizardDidIt)Bukkit.getServer().getPluginManager().getPlugin("AWizardDidIt");

        new BukkitRunnable(){
            @Override
            public void run() {
                long time = p.getWorld().getTime();
                if ( time > 0 && time < 13000 ) {
                    Bukkit.broadcastMessage( ChatColor.GREEN +  p.getDisplayName() + ChatColor.YELLOW
                            + " has invoked " + ChatColor.LIGHT_PURPLE + spellName + "!");
                    cancel();
                    return;
                } else if ( time >= 13000 && time < 23000 ) {
                    time += 100;
                    p.getWorld().setTime( time );
                } else {
                    time += 10;
                    p.getWorld().setTime( time );
                }
            }
        }.runTaskTimer( plugin, 0, 1 );
    }
}
