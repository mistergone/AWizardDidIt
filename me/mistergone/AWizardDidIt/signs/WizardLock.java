package me.mistergone.AWizardDidIt.signs;

import me.mistergone.AWizardDidIt.baseClasses.MagicSign;
import me.mistergone.AWizardDidIt.baseClasses.SignFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import net.minecraft.server.v1_15_R1.DoubleBlockFinder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class WizardLock extends MagicSign {

    public WizardLock() {
        signName = "Wizard Lock";
        signature = "[WizardLock]";
        cost = 0;

        signFunction = new SignFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );

            }
        };

    }


    public static void handleSignEvent(SignChangeEvent event) {
        String[] lines = event.getLines();
        Player p = event.getPlayer();
        Block b = event.getBlock();

        if ( !Tag.WALL_SIGNS.isTagged( event.getBlock().getType() ) ) {
            p.sendMessage( ChatColor.RED + "Wizard Lock must be a wall sign!");
            lines[0] = "Wizard Lock must";
            lines[1] = "be a wall sign.";
            return;
        }

        Block down = b.getRelative( BlockFace.DOWN );
        Boolean downIsChest = ( down.getType() == Material.CHEST ||
                down.getType() == Material.TRAPPED_CHEST );

        if ( downIsChest ) {
            boolean isEmpty = true;
            Chest chest = (Chest) down.getState();
            ItemStack[] inv = chest.getInventory().getContents();
            for (ItemStack s : inv ) {
                if ( s != null ) {
                    isEmpty = !true;
                    break;
                }
            }
            if ( !isEmpty ) {
                p.sendMessage( ChatColor.RED + "Wizard Lock cannot be placed over a chest with items in it!");
                lines[0] = "The chest below";
                lines[1] = "must be empty.";
                return;
            }
        }

        lines[0] = "[" + ChatColor.DARK_PURPLE + "WizardLock" + ChatColor.BLACK + "]";
        lines[3] = ChatColor.LIGHT_PURPLE + p.getName();
        p.playSound( b.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 0.5F, 1F);

//        if ( ( lines[2] == null ) || Integer.valueOf( lines[2] ) > 10 ) {
//            lines[2] = "5";
//        }
    }

    public static Boolean isWizardLockSign( Block b ) {
        if ( !Tag.WALL_SIGNS.isTagged(  b.getType() ) ) return false;
        Sign sign = (Sign) b.getState();
        String[] lines = sign.getLines();
        if ( !( ChatColor.stripColor( lines[0] ). equals( "[WizardLock]" ) ) ) return false;

        return true;
    }
}
