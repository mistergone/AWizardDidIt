package me.mistergone.AWizardDidIt.helpers;

import org.bukkit.entity.Player;

public class ExpManager {
    private static final int[] levels = new int[] {
                 0, // 0
                 7, // 1
                16, // 2
                27, // 3
                40, // 4
                55, // 5
                72, // 6
                91, // 7
                112, // 8
                135, // 9
                160, // 10
                187, // 11
                216, // 12
                247, // 13
                280, // 14
                315, // 15
                352, // 16
                394, // 17
                441, // 18
                493, // 19
                550, // 20
                612, // 21
                679, // 22
                751, // 23
                828, // 24
                910, // 25
                997, // 26
                1089, // 27
                1186, // 28
                1288, // 29
                1395, // 30
                1507, // 31
                1628, // 32
                1758, // 33
                1897, // 34
                2045, // 35
                2202, // 36
                2368, // 37
                2543, // 38
                2727, // 39
                2920 // 40
    };

    public static int getLevelBelow( int exp ) {
        int level = 0;
        int expRequired = levels[ level ];

        while ( expRequired <= exp ) {
            level++;
            expRequired = levels[ level ];
        }

        return level - 1;
    }

    public static int getExpTotal( Player p ) {
        int level = p.getLevel();
        int toLevel = (int)( p.getExpToLevel() * p.getExp() );
        int totalExp = levels[level] + toLevel;

        return totalExp;
    }


    public static Boolean spendExp( Player player, int amount ) {
        int total = getExpTotal( player );
        if ( total > amount ) {
            int newTotal = total - amount;
            int newLevel = getLevelBelow( newTotal );
            float progress = (float)( newTotal - levels[newLevel] ) / ( levels[newLevel +1 ] - levels[newLevel] );
            player.setLevel( newLevel );
            player.setExp( progress );
            return true;
        } else {
            return false;
        }
    }

}
