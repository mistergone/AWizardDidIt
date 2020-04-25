package me.mistergone.AWizardDidIt.signs;

import me.mistergone.AWizardDidIt.baseClasses.MagicSign;
import me.mistergone.AWizardDidIt.baseClasses.SignFunction;
import me.mistergone.AWizardDidIt.helpers.SignHelper;
import me.mistergone.AWizardDidIt.helpers.SpecialEffects;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class SortingChest extends MagicSign {

    public SortingChest() {
        signName = "Sorting Chest";
        signature = "[SortingChest]";
        cost = 5;

        signFunction = new SignFunction() {
            @Override
            public void run() {
                // find out if there's a chest under this
                Block under = signBlock.getRelative( BlockFace.DOWN );
                if ( under.getType() != Material.CHEST && under.getType() != Material.TRAPPED_CHEST ) {
                    player.sendMessage( "To invoke " + signName + ", the block under the sign must be a chest!");
                    return;
                }

                Chest distChest = (Chest) under.getState();
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );

                HashMap<Material, Chest> chestIndex = new HashMap<>();
                // create the chestIndex
                List<Entity> entities = player.getNearbyEntities( 20, 10, 20 );
                for ( Entity e : entities ) {
                    if ( e instanceof ItemFrame) {
                        Block b = e.getWorld().getBlockAt( e.getLocation() ).getRelative( BlockFace.DOWN );
                        if (  b.getType() == Material.CHEST ) {
                            ItemStack inFrame = ( (ItemFrame)e ).getItem();
                            chestIndex.put( inFrame.getType(), (Chest)b.getState() );
                        }
                    }
                }

                int movedItems = 0;
                int leftoverItems = 0;
                int failedMoves = 0;
                int notFound = 0;

                Inventory distInv = distChest.getInventory();
                for ( int i = 0; i < distInv.getSize(); i ++ ) {
                    ItemStack item = distInv.getContents()[i];
                    if ( item != null && chestIndex.keySet().contains( item.getType() ) ) {
                        if ( !wizardPlayer.spendWizardPower( cost, signName ) ) return;
                        // Special effects!
                        SpecialEffects.portalCollapse( signBlock.getLocation() );
                        player.playSound( player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_WOLOLO, 3F, 2F );

                        Chest chest = chestIndex.get( item.getType() );
                        Inventory inv = chest.getInventory();
                        int startingAmt = item.getAmount();
                        HashMap<Integer, ItemStack> leftovers = inv.addItem( item );
                        if ( leftovers.size() > 0 ) {
                            ItemStack left = leftovers.get(0);
                            if ( left.getAmount() == startingAmt ) {
                                failedMoves++;
                            } else {
                                leftoverItems++;
                                movedItems++;
                            }
                            item.setAmount( left.getAmount() );
                        } else {
                            distChest.getInventory().setItem( i, null );
                            movedItems++;
                        }
                    } else if ( item != null ) {
                        notFound++;
                    }
                }

                if ( movedItems > 0 || failedMoves > 0 || notFound > 0 ) {
                    player.sendMessage( ChatColor.GREEN + "You have invoked " + signName + "!" );
                    String message = ChatColor.GREEN + "";
                    if ( movedItems > 0 ) message += String.valueOf( movedItems) + " item stack(s) were moved. ";
                    if ( leftoverItems > 0 ) message += String.valueOf( leftoverItems ) + " item stack(s) had leftover items. " ;
                    if ( failedMoves > 0 ) message += ChatColor.RED + String.valueOf( failedMoves ) + " item stack(s) could not be moved. ";
                    if ( notFound > 0 ) message += ChatColor.RED + String.valueOf( notFound) + " item stack(s) did not have marked storage chests in range. ";
                    player.sendMessage( message );
                } else if ( movedItems == 0 && failedMoves == 0 && notFound == 0 ) {
                    player.sendMessage( ChatColor.RED + signName + " could not find items to move." );
                }

            }
        };
    }

    public static void handleSignEvent(SignChangeEvent event) {
        String[] lines = event.getLines();
        Player p = event.getPlayer();

        if ( !Tag.WALL_SIGNS.isTagged( event.getBlock().getType() ) ) {
            p.sendMessage( ChatColor.RED + "Sorting Chest must be a wall sign!");
            lines[0] = "Sorting Chest";
            lines[1] = "must be";
            lines[2] = "a wall sign.";
            return;
        }

        lines[0] = "[" + ChatColor.DARK_PURPLE + "SortingChest" + ChatColor.BLACK + "]";
        lines[3] = "";

    }
}
