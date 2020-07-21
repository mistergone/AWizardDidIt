package me.mistergone.AWizardDidIt.helpers;

import me.mistergone.AWizardDidIt.baseClasses.MagicSign;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class SignHelper {
    // sign helpers
    public static Boolean isWizardSign( Block b ) {
        if ( !( b.getType().toString().contains( "SIGN" ) ) ) return false;
        Sign sign = (Sign) b.getState();
        String[] lines = sign.getLines();
        String signature = ChatColor.stripColor(lines[0].trim());
        MagicSign magicSign = getWizardry().getMagicSign(signature);
        if ( magicSign != null ) return true;

        return false;
    }

    public static ArrayList<Block> getAttachedSigns(Block b ) {
        ArrayList<Block> signs = new ArrayList<>();
        for ( BlockFace face: BlockHelper.sides ) {
            Block check = b.getRelative( face );
            if ( Tag.WALL_SIGNS.isTagged( check.getType() ) ) {
                WallSign sign = (WallSign) check.getBlockData();
                if ( sign.getFacing() == face ) {
                    signs.add( check );
                }
            }
        }
        return signs;
    }

    public static Boolean hasAttachedWizardSigns(Block b ) {
        ArrayList<Block> signs = getAttachedSigns( b );
        for ( Block signBlock : signs ) {
            if ( isWizardSign( signBlock) ) return true;
        }

        return false;
    }

    public static Boolean hasSignOpposite(Block b, BlockFace clickedFace ) {
        Block check = b.getRelative( clickedFace.getOppositeFace() );
        if ( Tag.WALL_SIGNS.isTagged( check.getType() ) ) {
            WallSign sign = (WallSign) check.getBlockData();
            if ( sign.getFacing() == clickedFace.getOppositeFace() ) {
                return true;
            }
        }
        return false;
    }

    public static String getSignOwner(Sign s ) {
        String[] lines = s.getLines();
        return  ChatColor.stripColor( lines[3] );
    }

    public static Block getAttachedBlock( Block signBlock ) {
        if ( !Tag.WALL_SIGNS.isTagged( signBlock.getType() ) ) return null;
        WallSign sign = (WallSign) signBlock.getBlockData();
        Block b = null;

        return b;
    }
}
