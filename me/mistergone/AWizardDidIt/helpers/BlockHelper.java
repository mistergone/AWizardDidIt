package me.mistergone.AWizardDidIt.helpers;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockHelper {
    // BlockManager is a set of methods for making a "box" of Blocks, which is an ArrayList
    // that represents a rectangular prism of Blocks in the World. It also contains some helpful
    // statics

    public static final List EMPTY_BLOCK_TYPES = Arrays.asList( new Material[] {
            Material.AIR,
            Material.VOID_AIR,
            Material.CAVE_AIR
    } );

    // Things that Silk Touch works on, for a pick
    public static final List SILKY_PICK_TYPES = Arrays.asList( new Material[] {
            Material.BEE_NEST,
            Material.BEEHIVE,
            Material.BLUE_ICE,
            Material.BOOKSHELF,
            Material.CAMPFIRE,
            Material.CLAY,
            Material.COAL_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.ENDER_CHEST,
            // Glass types
            Material.GLASS,
            Material.GLASS_PANE,
            Material.GLOWSTONE,
            Material.GRASS_BLOCK,
            Material.GRAVEL,
            Material.ICE,
            Material.LAPIS_ORE,
            Material.ENDER_CHEST,
            Material.MELON,
            // MUSHROOM blocks
            Material.BROWN_MUSHROOM_BLOCK,
            Material.RED_MUSHROOM_BLOCK,
            Material.REDSTONE_ORE,
            Material.MYCELIUM,
            Material.NETHER_QUARTZ_ORE,
            Material.NETHER_GOLD_ORE,
            // NYLIUM types
            Material.CRIMSON_NYLIUM,
            Material.WARPED_NYLIUM,
            Material.PODZOL,
            Material.REDSTONE_ORE,
            Material.SEA_LANTERN,
            Material.STONE,
            Material.TURTLE_EGG
    } );

    public static List<Material> airTypes = new ArrayList<Material>( Arrays.asList( Material.AIR, Material.CAVE_AIR ) );

    public static List<BlockFace> sides = new ArrayList<>( Arrays.asList( BlockFace.NORTH, BlockFace.EAST,
            BlockFace.SOUTH, BlockFace.WEST ) );

    /**
     * Returns whether a Material is a "silky pick type"
     * @param material The Material in question
     * @return Boolean True if material is a silky pick type
     */

    public static Boolean isSilkyPickType( Material material ) {
        if ( SILKY_PICK_TYPES.contains( material ) ) {
            return true;
        } else if ( Tag.CORALS.isTagged( material ) || Tag.CORAL_BLOCKS.isTagged( material ) || Tag.CORAL_PLANTS.isTagged( material )  ) {
            // Coral is silk pickable!
            return true;
        } else if ( Tag.LEAVES.isTagged( material ) ) {
            // Leaves are silk pickable
            return true;
        } else if ( material.toString().contains("STAINED_GLASS") ) {
            return true;
        }

        return false;
    }

    /**
     * Gets a box of Blocks based on a block and a facing. The box selection is in the OPPOSITE direction
     * of the BlockFace 'face' parameter, so the clicked face can be passed easily as a parameter. The box is
     * 'square' on one side, because the width/height of the box are indistinguishable.
     * @param block The Block to start with
     * @param face The facing OPPOSITE of the direction of the box
     * @param size The height and width of the box, which are perpendicular to the 'face' direction
     * @param depth The depth of the box, which parallel to the 'face' direction
     * @return ArrayList<Block> An Array of Blocks
     */
    public static ArrayList<Block> getSquareBoxFromFace( Block block, BlockFace face, int size, int depth ) {
        ArrayList<Block> blockBox = new ArrayList<>();
        BlockFace opp = face.getOppositeFace();
        // Even number size will be rounded up
        if ( size % 2 == 0 ) {
            size += 1;
        }
        int x = -1 * (int)Math.floor( size / 2 );
        int xMod = 1;
        int xMax = x + size - 1;
        int y = -1 * (int)Math.floor( size / 2 );
        int yMod = 1;
        int yMax = y + size - 1;
        int z = -1 * (int)Math.floor( size / 2 );
        int zMod = 1;
        int zMax = z + size - 1;
        if ( opp.getModX() != 0 ) {
            x = 0;
            xMod = xMod * opp.getModX();
            xMax = depth - 1;
        } else if ( opp.getModY() != 0 ) {
            y = 0;
            yMod = yMod * opp.getModY();
            yMax = depth - 1;
        } else if ( opp.getModZ() != 0 ) {
            z = 0;
            zMod = zMod * opp.getModZ();
            zMax = depth - 1;
        }
        for ( int a = x; a <= xMax; a++ ) {
            for ( int b = y; b <= yMax; b++ ) {
                for ( int c = z; c <= zMax; c++ ) {
                    blockBox.add( block.getRelative( a * xMod, b * yMod, c * zMod ) );
                }
            }
        };

        return blockBox;
    }

    public static ArrayList<Block> getCubeByRadius( Block block, int radius ) {
        ArrayList<Block> blockBox = new ArrayList<>();
        Location loc = block.getLocation();
        for ( int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++  ) {
            for ( int y = loc.getBlockY() - radius; y <= loc.getBlockY() + radius; y++ ) {
                for ( int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++ ) {
                    blockBox.add( loc.getWorld().getBlockAt( x, y, z ) );
                }
            }
        }
        return blockBox;
    }

    public static ArrayList<Block> getBoxByDimensions( Block block, int xSize, int ySize, int zSize) {
        ArrayList<Block> blockBox = new ArrayList<>();
        Location loc = block.getLocation();
        for ( int x = loc.getBlockX() - xSize; x <= loc.getBlockX() + xSize; x++  ) {
            for ( int y = loc.getBlockY() - ySize; y <= loc.getBlockY() + ySize; y++ ) {
                for ( int z = loc.getBlockZ() - zSize; z <= loc.getBlockZ() + zSize; z++ ) {
                    blockBox.add( loc.getWorld().getBlockAt( x, y, z ) );
                }
            }
        }
        return blockBox;
    }

    public static BlockFace yawToFace( float yaw ) {
        double angle = Math.round( yaw );
        // Turn the angle if it's less than zero
        if ( angle < 0 ) angle += 360;
        if ( angle > 315 || angle <= 45 ) {
            return BlockFace.SOUTH;
        } else if ( angle > 45 && angle <= 135 ) {
            return BlockFace.WEST;
        } else if ( angle > 135 && angle <= 225 ) {
            return BlockFace.NORTH;
        } else {
            return BlockFace.EAST;
        }
    }

    public static String locToString( Location loc ) {
        String str = loc.getBlockX() + ", "
                + loc.getBlockY() + ", "
                + loc.getBlockZ();
        return str;
    }

    public static Boolean isDangerous( Material m ) {
        if ( m == Material.LAVA || m == Material.FIRE ) {
            return true;
        } else {
            return false;
        }
    }

    // Check if the block is another players sign, or is another player's locked chest, etc
    public static Boolean isSafeToBreak( Block block, Player player, String spellName ) {
        // Don't break wizard signs
        if ( SignHelper.isWizardSign( block ) ) {
            player.sendMessage( ChatColor.RED + spellName + " cannot break or replace Wizard Signs!" );
            return false;
        }

        // Don't break blocks with Wizard Signs attached
        if ( SignHelper.hasAttachedWizardSigns( block ) ) {
            player.sendMessage( ChatColor.RED + "A Wizard Sign is attached to one of these blocks! Please break the " +
                    "Wizard Sign first!");
            return false;
        }

        // Don't break chests if they're WizardLocked
        Material mat = block.getType();
        if ( mat == Material.CHEST || mat == Material.TRAPPED_CHEST ) {
            Chest c = (Chest) block.getState();
            Inventory i = c.getInventory();
            ArrayList<Block> ups = new ArrayList<>();
            if ( i instanceof DoubleChestInventory) {
                DoubleChestInventory doubleChest = (DoubleChestInventory) i;
                ups.add( doubleChest.getLeftSide().getLocation().getBlock().getRelative( BlockFace.UP ) );
                ups.add( doubleChest.getRightSide().getLocation().getBlock().getRelative( BlockFace.UP ) );
            } else {
                ups.add( block.getRelative( BlockFace.UP ) );
            }

        }

        return true;
    }

    /* Check if type is empty */
    public static Boolean isEmptyBlockType( Material material ) {
        if (EMPTY_BLOCK_TYPES.contains(material)) {
            return true;
        } else {
            return false;
        }
    }

}
