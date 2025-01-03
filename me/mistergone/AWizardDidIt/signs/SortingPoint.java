package me.mistergone.AWizardDidIt.signs;

import me.mistergone.AWizardDidIt.baseClasses.MagicSign;
import me.mistergone.AWizardDidIt.baseClasses.SignFunction;
import me.mistergone.AWizardDidIt.helpers.ItemHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

public class SortingPoint extends MagicSign {

    public SortingPoint() {
        signName = "Sorting Point";
        signature = "[SortingPoint]";
        cost = 0;

        signFunction = new SignFunction() {
            @Override
            public void run() {

            }
        };


    }

    public static void handleSignEvent(SignChangeEvent event) {
        String[] lines = event.getLines();
        Player p = event.getPlayer();

        if ( !Tag.WALL_SIGNS.isTagged( event.getBlock().getType() ) ) {
            p.sendMessage( ChatColor.RED + "Sorting Point must be a wall sign!");
            lines[0] = "Sorting Point";
            lines[1] = "must be";
            lines[2] = "a wall sign.";
            return;
        }

        lines[0] = "[" + ChatColor.DARK_AQUA + "SortingPoint" + ChatColor.BLACK + "]";
        String secondLine = lines[1];
        if ( secondLine.equals( "" ) || secondLine.toLowerCase().equals( "automatic" ) ) {
            lines[1] = ChatColor.LIGHT_PURPLE + "Automatic";
        } else {
            for ( int x = 1; x <= 3; x++ ) {
                String line = lines[x];
                line = line.trim().replace( " ", "_" );
                if ( line.equals( "" ) ) continue;
                if ( Material.matchMaterial( line.toUpperCase() ) == null ) {
                    p.sendMessage( "Could not find Material: " + line );
                    lines[x] = "";
                } else {
                    lines[x] = ChatColor.LIGHT_PURPLE + line.toLowerCase();
                }
            }
        }
    }

    public static Boolean isSortingPointSign( Block b ) {
        if ( !Tag.WALL_SIGNS.isTagged(  b.getType() ) ) return false;
        Sign sign = (Sign) b.getState();
        String[] lines = sign.getSide(Side.FRONT).getLines();
        if ( !( ChatColor.stripColor( lines[0] ). equals( "[SortingPoint]" ) ) ) return false;

        return true;
    }

}
