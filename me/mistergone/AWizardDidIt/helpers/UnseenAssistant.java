package me.mistergone.AWizardDidIt.helpers;

import me.mistergone.AWizardDidIt.AWizardDidIt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static me.mistergone.AWizardDidIt.data.UnseenProjectManager.getUnseenPM;

public class UnseenAssistant {
    private WizardPlayer wizardPlayer;
    private int currentTask;
    private String isWorking;
    private ArrayList<Block> blockList;
    private String currentProject;
    private HashMap<String, Integer> bounds;
    private Location origin;
    private Location currentLoc;
    private Block originSign;
    private ArrayList<Chest> chests;
    private ArrayList<Material> notFound;
    private ArrayList<Material> airTypes = new ArrayList<>();
    private ArrayList<BlockFace> horizontals;


    public UnseenAssistant( WizardPlayer wizardPlayer ) {
        this.wizardPlayer = wizardPlayer;
        airTypes.add(Material.AIR);
        airTypes.add(Material.CAVE_AIR);

        chests = new ArrayList<Chest>();
    }

    public void architectClone( String projectKey, Block signBlock, String command ) {
        Location[] points = getUnseenPM().getProjectPoints( projectKey );
        String direction = "SE";
        originSign = signBlock;
        currentProject = projectKey.split(":")[1] ;
        bounds = new HashMap<>();
        notFound = new ArrayList<>();
        horizontals = new ArrayList<BlockFace>(Arrays.asList(
                BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST,
                BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST
        ) );

        if (points[0] != null && points[1] != null) {
            bounds.put( "xMin", Math.min(points[0].getBlockX(), points[1].getBlockX()) + 1 );
            bounds.put( "xMax", Math.max(points[0].getBlockX(), points[1].getBlockX()) - 1 );
            bounds.put( "yMin", Math.min(points[0].getBlockY(), points[1].getBlockY()) );
            bounds.put( "yMax", Math.max(points[0].getBlockY(), points[1].getBlockY()) );
            bounds.put( "zMin", Math.min(points[0].getBlockZ(), points[1].getBlockZ()) + 1 );
            bounds.put( "zMax", Math.max(points[0].getBlockZ(), points[1].getBlockZ()) - 1 );
        }
        int xDiff = bounds.get("xMax") - bounds.get("xMin") + 1;
        int zDiff = bounds.get("zMax") - bounds.get("zMin") + 1;
        if ( command.length() > 5 ) {
            direction = command.substring( 6 );
        }

        if ( direction.equalsIgnoreCase( "SE" ) ) {
            origin = signBlock.getRelative( BlockFace.SOUTH_EAST ).getLocation();
            horizontals.remove( BlockFace.SOUTH_EAST );
        } else if ( direction.equalsIgnoreCase( "SW" ) ) {
            origin = signBlock.getRelative( BlockFace.SOUTH, 1 ).getRelative( BlockFace.WEST, xDiff ).getLocation();
            horizontals.remove( BlockFace.SOUTH_WEST );
        } else if ( direction.equalsIgnoreCase( "NE" ) ) {
            origin = signBlock.getRelative( BlockFace.EAST, 1 ).getRelative( BlockFace.NORTH, zDiff ).getLocation();
            horizontals.remove( BlockFace.NORTH_EAST );
        } else if ( direction.equalsIgnoreCase( "NW" ) ) {
            origin = signBlock.getRelative( BlockFace.NORTH, zDiff ).getRelative( BlockFace.WEST, xDiff ).getLocation();
            horizontals.remove( BlockFace.NORTH_WEST );
        }

        currentLoc = new Location( signBlock.getWorld(), bounds.get("xMin"), bounds.get("yMin"), bounds.get("zMin") );

        // Clean up the chests property
        if ( chests != null ) chests.clear();
        // Now find surrounding chests

        for ( BlockFace face: horizontals ) {
            Block b = signBlock.getRelative( face );
            if ( b.getLocation().equals( origin ) ) continue;
            if ( b != null && b.getType() == Material.CHEST ) {
                if ( b.getState() instanceof Chest ) {
                    Chest c = (Chest) b.getState();
                    chests.add( c );
                }
            }
        }

        // No chests? No work!
        if ( chests == null || chests.size() == 0 ) {
            wizardPlayer.getPlayer().sendMessage( ChatColor.RED + "Your Unseen Assistant could not find any valid chests from which to create your project! This task has been cancelled." );
            return;
        }

        isWorking = "Your Unseen Assistant is busy building the architecture project \"" + currentProject + "\".";
        Bukkit.getScheduler().cancelTask( currentTask );

        currentTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                (AWizardDidIt)Bukkit.getServer().getPluginManager().getPlugin("AWizardDidIt"),
                new Runnable() {
                    @Override
                    public void run() {
                        cloneSome();
                    }
                }, 0, 10 );

        wizardPlayer.getPlayer().sendMessage( ChatColor.YELLOW + "Your Unseen Assistant has begun working on the architecture project, \"" + currentProject + ".\"");

    }

    public void cloneSome() {
        Block block = origin.getWorld().getBlockAt( this.currentLoc );
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        SpecialEffects.magicChest( originSign.getLocation() );

        for ( int i = 0; i < 100; i ++ ) {
            // Get block at currentLoc
            block = origin.getWorld().getBlockAt( x, y, z );

            // Handle the cloning
            Block clone = block.getWorld().getBlockAt(origin).getRelative(x - bounds.get("xMin"), y - bounds.get("yMin"), z - bounds.get("zMin"));

            // If they're not already the same type and they are solid, attempt to clone
            if ( clone.getType() != block.getType() && block.getType().isSolid() ) {
                ItemStack item = new ItemStack( block.getType(), 1 );
                HashMap<Integer, ItemStack> notRemoved = null;
                for ( Chest chest: chests ) {

                    notRemoved = chest.getInventory().removeItem(item);
                    if ( notRemoved.size() > 0 ) {
                        continue;
                    } else {
                        break;

                    }
                }
                if ( notRemoved != null && notRemoved.size() > 0) {
                    if (!notFound.contains(item.getType())) {
                        wizardPlayer.getPlayer().sendMessage(ChatColor.RED + "Unseen Architect could not find "
                                + ChatColor.BLUE + item.getType().toString() + ChatColor.RED + "!");
                        notFound.add(item.getType());

                    }
                } else {
                    if ( wizardPlayer.spendUnseenEnergy( 1 ) ) {
                        clone.setType(block.getType());
                    } else {
                        wizardPlayer.getPlayer().sendMessage(ChatColor.RED + "You do not have sufficient Wizard Power to empower your Unseen Assistant for this task!");
                    }
                }

            } else if ( airTypes.contains( block.getType() ) && !airTypes.contains( clone.getType() ) ) {
                if ( wizardPlayer.spendUnseenEnergy( 1 ) ) {
                    clone.breakNaturally();
                } else {
                    wizardPlayer.getPlayer().sendMessage(ChatColor.RED + "You do not have sufficient Wizard Power to empower your Unseen Assistant for this task!");
                }
            }

            // Move currentLoc
            Boolean atXMax = ( x == bounds.get( "xMax") );
            Boolean atYMax = ( y == bounds.get( "yMax") );
            Boolean atZMax = ( z == bounds.get( "zMax") );
            if ( atXMax && atYMax && atZMax ) {
                wizardPlayer.getPlayer().sendMessage( ChatColor.GOLD + "Your Unseen Assistant has finished the architecture project, \"" + currentProject + "\"." );
                Bukkit.getScheduler().cancelTask( currentTask );
                currentProject = null;
                isWorking = null;
                break;
            } else if ( atZMax && atYMax ) {
                y = bounds.get( "yMin" );
                z = bounds.get( "zMin" );
                x++;
            } else if ( atZMax ) {
                z = bounds.get( "zMin" );
                y++;
            } else {
                z++;
            }
        }

        currentLoc = block.getLocation();

    }

    public Boolean cancelTask() {
        if ( isWorking != null ) {
            Bukkit.getScheduler().cancelTask( currentTask );
            isWorking = null;
            return true;
        } else {
            return false;
        }
    }

    public String getIsWorking() {
        return isWorking;
    }
}
