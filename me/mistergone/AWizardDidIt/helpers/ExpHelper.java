package me.mistergone.AWizardDidIt.helpers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ExpHelper {

    public static int getLevelBelow( int exp ) {
        int level = 0;
        int expRequired = getExpForLevel( level );

        while ( expRequired <= exp ) {
            level++;
            expRequired = getExpForLevel( level );
        }

        return level - 1;
    }

    public static int getExpForLevel( int level ) {
        int expForLevel;
        if ( level <= 16 ) {
            expForLevel = level * level + ( 6 * level );
        } else if ( level  <= 31 ) {
            expForLevel = (int)( ( 2.5 * level * level ) - ( 40.5 * level ) + 360 );
        } else {
            expForLevel = (int)( ( 4.5 * level * level ) - ( 162.5 * level ) + 2220 );
        }

        return expForLevel;
    }

    public static int getExpTotal( Player p ) {
        int level = p.getLevel();
        int levelExp = getExpForLevel( level );
        int toLevel = (int)Math.ceil( p.getExpToLevel() * p.getExp() );
        int totalExp = levelExp + toLevel;

        return totalExp;
    }


    public static Boolean spendExp( Player player, int amount ) {
        int total = getExpTotal( player );
        if ( total >= amount ) {
            int newTotal = total - amount;
            int newLevel = getLevelBelow( newTotal );
            float progress = (float)( newTotal - getExpForLevel( newLevel ) ) / ( getExpForLevel( newLevel + 1 ) - getExpForLevel( newLevel ) );
            player.setLevel( newLevel );
            player.setExp( progress );

            return true;
        } else {
            return false;
        }
    }

}
