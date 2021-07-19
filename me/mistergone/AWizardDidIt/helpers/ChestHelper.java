package me.mistergone.AWizardDidIt.helpers;

import me.mistergone.AWizardDidIt.signs.WizardLock;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class ChestHelper {

    public static boolean isWizardLocked ( Chest c, Player p ) {
        Inventory i = c.getInventory();
        ArrayList<Block> ups = new ArrayList<>();
        if ( i instanceof DoubleChestInventory) {
            DoubleChestInventory doubleChest = (DoubleChestInventory) i;
            ups.add( doubleChest.getLeftSide().getLocation().getBlock().getRelative( BlockFace.UP ) );
            ups.add( doubleChest.getRightSide().getLocation().getBlock().getRelative( BlockFace.UP ) );
        } else {
            ups.add( c.getBlock().getRelative( BlockFace.UP ) );
        }

        for ( Block u: ups ) {
            if ( WizardLock.isWizardLockSign( u ) ) {
                if ( p == null ) return true;
                Sign s = (Sign) u.getState();
                String owner = SignHelper.getSignOwner( s );
                if ( owner.equals( p.getName() ) ) {
                    return false;
                } else {
                    return true;
                }

            }
        }

        return false;
    }
}
