package me.mistergone.AWizardDidIt.signs;

import me.mistergone.AWizardDidIt.baseClasses.MagicSign;
import me.mistergone.AWizardDidIt.baseClasses.SignFunction;
import me.mistergone.AWizardDidIt.helpers.BlockHelper;
import me.mistergone.AWizardDidIt.helpers.SignHelper;
import me.mistergone.AWizardDidIt.helpers.SpecialEffects;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class SortingChest extends MagicSign {

    public SortingChest() {
        signName = "Sorting Chest";
        signature = "[SortingChest]";
        cost = 25;

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

                HashMap<Material, ArrayList<Chest>> chestIndex = new HashMap<>();
                HashMap<String, Chest> stringIndex = new HashMap<>();

                // add Item Frames to chestIndex
                List<Entity> entities = player.getNearbyEntities( 20, 10, 20 );
                for ( Entity e : entities ) {
                    if ( e instanceof ItemFrame) {
                        Block chestCheck = e.getWorld().getBlockAt( e.getLocation() ).getRelative( BlockFace.DOWN );
                        if (  chestCheck.getType() == Material.CHEST ) {
                            ItemStack inFrame = ( (ItemFrame)e ).getItem();
                            if ( inFrame == null ) continue;
                            Material frameMat = inFrame.getType();
                            ArrayList<Chest> chests = chestIndex.get( frameMat );
                            if ( chests == null ) {
                                chests = new ArrayList<Chest>();
                            }
                            chests.add( (Chest)chestCheck.getState() );
                            chestIndex.put( frameMat, chests );
                        }
                    }
                }

                // add Signs to chestIndex & stringIndex
                Location signLoc = signBlock.getLocation();
                Block start = player.getWorld().getBlockAt(
                        signLoc.getBlockX() - 20,
                        signLoc.getBlockY() - 10,
                        signLoc.getBlockZ() - 20
                );

                List<Block> blocks = BlockHelper.getBoxByDimensions( start, 40, 20, 40 );
                for( Block b : blocks ) {
                    if ( !SortingPoint.isSortingPointSign( b ) ) continue;
                    Block chestCheck = b.getRelative(BlockFace.DOWN );
                    if ( chestCheck.getType() != Material.CHEST ) continue;

                    Sign sign = (Sign)b.getState();
                    String[] lines = sign.getSide(Side.FRONT).getLines();
                    Chest chest = (Chest) chestCheck.getState();
                    if ( chest.getInventory().firstEmpty() == -1 ) continue;
                    if ( ChatColor.stripColor( lines[1] ).equals("Automatic") ) {
                        Inventory sorter = chest.getInventory();
                        for (  ItemStack i: sorter.getContents() ) {
                            if ( i == null ) continue;
                            Material m = i.getType();
                            ArrayList<Chest> chests = chestIndex.get( m );
                            if ( chests == null ) {
                                chests = new ArrayList<Chest>();
                            }
                            chests.add( chest );
                            chestIndex.put( m, chests );
                        }
                    } else {
                        for ( int x = 1; x <=3; x++ ) {
                            String line = ChatColor.stripColor( lines[x] ).toUpperCase();
                            // Add wildcard signs to stringIndex
                            if ( line.contains("*") ) {
                                stringIndex.put( line.replace("*", "" ).toLowerCase().trim(), chest );
                            } else {
                                Material m = Material.matchMaterial( line );
                                if ( m == null ) continue;
                                ArrayList<Chest> chests = chestIndex.get( m );
                                if ( chests == null ) {
                                    chests = new ArrayList<Chest>();
                                }
                                chests.add( chest );
                                chestIndex.put( m, chests );
                            }
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
                    if ( item == null ) continue;
                    ArrayList<Chest> chests = new ArrayList<>();
                    if ( chestIndex.containsKey( item.getType() ) ) {
                        chests = chestIndex.get(item.getType());
                    }

                    // Add chests with wildcard signs
                    String mat = item.getType().toString().toLowerCase().trim();
                    for ( HashMap.Entry<String,Chest> matchItem: stringIndex.entrySet() ) {
                        String wildcard = matchItem.getKey().toLowerCase().trim();
                        if ( mat.contains( wildcard ) ) {
                            chests.add( matchItem.getValue() );
                        }
                    }

                    if ( item != null && chests.size() == 0 ) {
                        notFound++;
                        continue;
                    }

                    // We have a chest(s), now go!
                    if (!wizardPlayer.spendWizardPower(cost, signName)) return;

                    int startingAmt = item.getAmount();
                    while (item.getAmount() > 0 && chests.size() > 0) {
                        // try to add the item to chests[0]
                        Chest chest = chests.get(0);
                        Inventory inv = chest.getInventory();
                        HashMap<Integer, ItemStack> leftovers = inv.addItem(item);

                        // There were leftovers
                        if (leftovers.size() > 0) {
                            // Chest is full, remove the chest
                            chests.remove(0);

                            // Set itemStack to leftover amount
                            ItemStack left = leftovers.get(0);
                            item.setAmount(left.getAmount());
                        } else {
                            // No leftovers! Finish up.
                            distChest.getInventory().setItem(i, null);
                            movedItems++;
                            item.setAmount(0);
                        }

                        // If items are left, then all chests were full!
                        if ( item.getAmount() > 0 ) {
                            if ( item.getAmount() != startingAmt ) {
                                movedItems++;
                                leftoverItems++;
                            } else {
                                failedMoves++;
                            }
                            distChest.getInventory().setItem( i, item );
                        }
                    }
                }

                if ( movedItems > 0 || failedMoves > 0 || notFound > 0 ) {
                    // Special effects!
                    SpecialEffects.portalCollapse(signBlock.getLocation());
                    player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_WOLOLO, 3F, 2F);

                    player.sendTitle( ChatColor.GREEN + signName + "!", "" );
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

        lines[0] = "[" + ChatColor.DARK_AQUA + "SortingChest" + ChatColor.BLACK + "]";
        lines[3] = "";

    }
}
